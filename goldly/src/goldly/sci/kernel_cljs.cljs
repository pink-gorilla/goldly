(ns goldly.sci.kernel-cljs
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core :as p]
   [cljs.reader :refer [read-string]]
   ; sci
   [sci.core :as sci]
   [sci.async :as scia]
   [sci.impl.resolve :as sci-resolve]
   ; bindings
   ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated
                                      sci-lazy-ns-dict lazy-modules]]

   [goldly.sci.clojure-core :refer [cljns] :as clojure-core]
   ; loading of cljs source-code

   [cljs.core.async :refer [<! >! chan close!] :refer-macros [go]]
   [goldly.sci.loader.async-load :refer [async-load-fn]]
   [cemerick.url :as curl]))

(declare ctx-repl) ; since we want to add compile-sci to the bindings, we have to declare the ctx later

(defn ^:export require-async [& libspec] ; symbol
  (apply scia/require ctx-repl libspec))

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

;; requiring resolve

(defn resolve-if-possible [s]
  (try
    (resolve-symbol s)
    (catch :default _ex
      nil)))

(defn requiring-resolve [s]
  (if-let [fun (resolve-if-possible s)]
    (p/resolved fun)
    (let [libspec [(-> s namespace symbol)]
          ;_ (info "requiring-resolve requiring libspec: " libspec)
          require-p (require-async libspec)
          resolve-p (p/then require-p (fn [_require-result]
                                        ;(info "get-page-fn: lib loaded. resolving " s " .. ")
                                        (let [f (resolve-symbol s)]
                                          ;(info "get-page-fn resolved symbol: " s)
                                          ;(info "get-page-fn resolved fun: " f)
                                          f)))]
      (p/catch require-p (fn [require-error]
                           (error "requiring-resolve: failed to load ns: " libspec)
                           (error "requiring-resolve error: " require-error)
                           nil))
      resolve-p)))

(def !last-ns (atom @sci/ns))

;; allow printing

(enable-console-print!) ; this defines *print-fn*

(def output (atom ""))

(defn my-print-fn [& args]
  (.apply js/console.log js/console (into-array args))
  ; https://github.com/clojure/clojurescript/commit/da2fa520ae5cd55ade7e263ec3b9a2149eb12f82
  (let [args (apply str args)
        args (str args "\n")]
    (swap! output str args)
    ;(apply *print-fn* args)
    ))

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
                          (debug "sci-cljs compile result: " result)
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
                 'goldly.sci {'require-async require-async
                              'compile-sci compile-code
                              'compile-sci-async compile-code-async
                              'resolve-symbol-sci resolve-symbol
                              'requiring-resolve requiring-resolve}})

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


