(ns goldly.sci.kernel-cljs
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core]
   [cljs.reader :refer [read-string]]
   ; sci
   [sci.core :as sci]
   [sci.async :as scia]
   [sci.impl.resolve :as sci-resolve]
   ; bindings
   ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated
                                      sci-lazy-ns-dict lazy-modules]]
   [goldly.sci.load-shadow :refer [load-ext-shadow]]
   [goldly.sci.clojure-core :refer [cljns] :as clojure-core]
   ; loading of cljs source-code
   [goldly.service.core :refer [run-cb]]
   [clojure.string]))

(declare ctx-repl) ; since we want to add compile-sci to the bindings, we have to declare the ctx later

; from scittle
;(defn ^:export eval-string [s]
;  (try (sci/eval-string* @ctx s)
;       (catch :default e
;         (error/error-handler e (:src @ctx))
;         (let [sci-error? (isa? (:type (ex-data e)) :sci/error)]
;           (throw (if sci-error?
;                    (or (ex-cause e) e)
;                    e))))))

(defn ^:export compile-code [code]
  (try
    {:result (sci/eval-string* ctx-repl code)}
    (catch :default e
      (timbre/error "sci compile-code --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(defn resolve-symbol [sym]
  (sci-resolve/resolve-symbol ctx-repl sym))

(defn sci-ns-lookup [libname]
  ; (str libname)
  ;(debug "available lazy namespaces:" (pr-str sci-lazy-ns-dict))
  (debug "looking up module for sci-ns:" libname)
  (when-let [module-name (get sci-lazy-ns-dict libname)]
    (info "module for " libname ": " module-name)
    (get lazy-modules module-name)))

(defn add-sci-ns [ctx libname ns opts sci-ns sci-defs ns-vars]
  (info "creating sci ns: " sci-ns "ns-vars:" ns-vars "sci-defs" sci-defs)
  (let [mlns (sci/create-ns sci-ns)
        sci-ns-def (->> (map (fn [sci-def ns-var]
                               ;(info "ci-def:" sci-def "ns-var:" ns-var)
                               ;(when-let [joke (:joke mod)]
                               ;  (info "joke: " (joke)))
                               (when (= sci-def :add)
                                 (info "TEST: adding: " (ns-var 7 7)))

                               [sci-def (sci/new-var sci-def ns-var {:ns mlns})])
                             sci-defs ns-vars)
                        (into {}))]
    (info "sci/add-namespace! sci-ns: " libname " sci ns :" sci-ns "def: " sci-ns-def)
    (sci/add-namespace! ctx libname sci-ns-def)))

(defn load-module-ns [ctx libname ns opts sci-ns sci-def loadable]
  (-> (load-ext-shadow loadable)
      (.then
       (fn [ns-vars]
         (info "received ns-vars for sci-ns: " sci-ns "libname: " libname "ns: " ns)
         (add-sci-ns ctx libname ns opts sci-ns sci-def ns-vars)))))

(defn load-module [ctx libname ns opts sci-mod]
  (info "loading all namespaces for module: " libname)
  (let [promises (map (fn [[sci-ns {:keys [sci-def loadable]}]]
                        (load-module-ns ctx libname ns opts sci-ns sci-def loadable))
                      sci-mod)
        p-all (promesa.core/all promises) ; Given an array of promises, return a promise that is fulfilled when all the items in the array are fulfilled.
        ]
    (.then p-all
           (fn [_d]
             (info "all namespaces loaded for: " libname)
             ;(info "all data: " d)
             ;; empty map return value, SCI will still process `:as` and `:refer`
             {}))))

(defn load-module-test [ctx libname ns opts]
  (-> (js/Promise.resolve #js {:libfn (fn [] "result!")})
      (.then (fn [mod]
               (info "demo lib : " libname "did load: " mod "mod-clj:" (js->clj mod))
               (sci/add-class! ctx (:as opts) mod)
               (sci/add-import! ctx ns (:as opts) (:as opts))
               {:handled true}))))

; discover clj/cljs files in resources (can be jar or file)

(defn ns->filename [ns]
  (-> ns
      (clojure.string/replace #"\." "/")
      (clojure.string/replace #"\-" "_")))

(defn on-cljs-received [ctx libname ns opts resolve reject [event-type {:keys [result] :as data}]]
  (info "on-cljs-received: " event-type "data: " data)
  (when-let [code (:code result)]
    (when-not (clojure.string/blank? code)
      (let [eval-p (scia/eval-string+ ctx code)]
        (.then eval-p (fn [res]
                        (let [{:keys [val ns]} res]
                          (info "sci-cljs loader require - compile result: " res)
                          (when-let [as (:as opts)]
                             ;; import class in current namespace with reference to globally
                             ;; registed class
                            (warn "registering as: " as "in ns: " ns " to:" (symbol libname))
                            (sci/add-import! ctx ns (symbol libname) (:as opts)))
                          (resolve {:handled false}))))))))

(defn load-module-sci [{:keys [ctx libname ns opts property-path] :as d}]
  ; libname: bongo.trott ; the ns that gets compiled
  ; ns:  demo.notebook.applied-science-jsinterop ; the namespace that is using it
  ; opts: {:as bongo, :refer [saying]}
  ; ctx is the sci-context
  (info "load-sci-src" "libname:" libname "ns: " ns "opts:" opts)
  (let [filename (-> libname str ns->filename (str ".cljs"))]
    (info "loading filename: " filename)
    (js/Promise.
     (fn [resolve reject]
       (run-cb {:fun 'goldly.cljs.loader/load-file-or-res!
                :args [filename]
                :cb (partial on-cljs-received ctx libname ns opts resolve reject)
                :timeout 8000})))))

(defn async-load-fn
  [{:keys [libname opts ctx ns] :as d}]
  (let [sci-mod (sci-ns-lookup libname)]
    (cond
      (= libname "some_js_lib")
      (load-module-test ctx libname ns opts)

      sci-mod
      (load-module ctx libname opts ns sci-mod)

      :else
      (load-module-sci d))))

(def !last-ns (atom @sci/ns))

;; allow printing

(enable-console-print!) ; this defines *print-fn*

(def output (atom ""))

(defn my-print-fn [& args]
  ;(.apply js/console.log js/console (into-array args))
  ; https://github.com/clojure/clojurescript/commit/da2fa520ae5cd55ade7e263ec3b9a2149eb12f82
  (swap! output str args)
  ;(apply *print-fn* args)
  )

;(sci/alter-var-root sci/print-fn (constantly *print-fn*))
(sci/alter-var-root sci/print-fn (constantly my-print-fn))
(sci/alter-var-root sci/print-err-fn (constantly *print-err-fn*))

(defn ^:export compile-code-async [code]
  (try
    (sci/binding [;sci/out *out* ;; this enables println etc.
                   ; *print-fn*
                   ; sci/print-newline true
                   ; sci/print-fn (fn [s]
                   ;                (.log js/console "*print-fn*")
                   ;                (.log js/console s)
                   ;               )
                  sci/ns @!last-ns]
      (let [eval-p (scia/eval-string+ ctx-repl code)]
        (.then eval-p (fn [res]
                        (let [{:keys [val ns]} res
                              result  {:id nil
                                       :code code
                                       :value val
                                       :out @output
                                       :ns (str ns)}]
                          (reset! !last-ns ns)
                          (reset! output "")
                          (info "sci-cljs response: " result)
                          result)))))
    (catch :default e
      (timbre/error "sci compile-code-async --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(def rns (sci/create-ns 'cljs.reader nil))

(def ctx-static
  {:bindings bindings-generated
   :preset {:termination-safe false} ; was: true
   :namespaces (merge
                ns-generated   ; ns-static
                {'clojure.core {'require scia/require
                                'time (sci/copy-var clojure-core/time cljns)
                                'system-time (sci/copy-var system-time cljns)
                                'random-uuid random-uuid
                                'read-string (sci/copy-var read-string rns)
                                ;'println (sci/copy-var clojure.core/println cljns)
                               ; '*print-fn* (sci/copy-var clojure.core/println cljns)
                                }
                 'goldly.sci {'compile-sci compile-code
                              'compile-sci-async compile-code-async
                              'resolve-symbol-sci resolve-symbol}})

   :classes  {'js js/window :allow :all}
   ;:classes  {'js goog/global :allow :all} ; In JS hosts, to allow interop with anything, use the following config:
   ;:classes {'js js/goog.global
             ;:allow :all
            ; 'js goog.global ; this returns the same as window.
            ; 'console js/console
            ; 'String js/String
             ;'js2 js/window
             ;'window js/window
    ;         }
   :disable-arity-checks true ; from clerk
   :async-load-fn async-load-fn})

(def ctx-repl (sci/init ctx-static))

