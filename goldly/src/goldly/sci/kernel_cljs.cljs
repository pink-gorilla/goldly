(ns goldly.sci.kernel-cljs
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info error]]
   [promesa.core]
   ; sci
   [sci.core :as sci]
   [sci.async :as scia]
   [sci.impl.resolve :as sci-resolve]
   ; bindings
   ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated
                                      sci-lazy-ns-dict lazy-modules]]
   [goldly.sci.load-shadow :refer [load-ext-shadow]]))

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
           (fn [d]
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

(defn async-load-fn
  [{:keys [libname opts ctx ns]}]
  (let [sci-mod (sci-ns-lookup libname)]
    (cond
      (= libname "some_js_lib")
      (load-module-test ctx libname ns opts)

      sci-mod
      (load-module ctx libname opts ns sci-mod))))

(defn ^:export compile-code-async [code]
  (try
    (sci/binding [sci/out *out*] ;; this enables println etc.
      (scia/eval-string* ctx-repl code))
    (catch :default e
      (timbre/error "sci compile-code-async --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(def ctx-static
  {:bindings (assoc bindings-generated
                    'compile-sci compile-code
                    'compile-sci-async compile-code-async
                    'resolve-symbol-sci resolve-symbol)
   :preset {:termination-safe false} ; was: true
   :namespaces (merge
                ns-generated   ; ns-static
                {'clojure.core {'require scia/require}
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
