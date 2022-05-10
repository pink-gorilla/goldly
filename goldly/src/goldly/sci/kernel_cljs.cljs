(ns goldly.sci.kernel-cljs
  (:require
   ;#?(:clj  [clojure.core.async :refer [>! chan close! go]]
      ;:cljs 
   [cljs.core.async :refer [>! chan close!] :refer-macros [go]]
           ; )
   [goog.object :as g]
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [sci.core :as sci]
   [goldly.sci.sci-types]
   [sci.impl.resolve :as sci-resolve]
  ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated]]
   ;[goldly.sci.lazy :refer [load-fn]]
   ))

(defn add-lazy [namespaces]
  (assoc namespaces
         'snippets
         {'add (sci/new-var 'add :internal)}))

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

(defn compile-code [code]
  (try
    {:result (sci/eval-string* ctx-repl code)
        ;(sci/eval-string code ctx-repl)
     }
    (catch :default e
      ;(error "sci compile-code --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(defn resolve-symbol [sym]
  (sci-resolve/resolve-symbol ctx-repl sym))

(def ctx-static
  {:bindings (assoc bindings-generated
                    'compile-sci compile-code
                    'resolve-symbol-sci resolve-symbol)
   :preset {:termination-safe false} ; was: true
   :namespaces (add-lazy ns-generated) ; ns-static

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
   ;:load-fn load-fn
   })

(def ctx-repl (sci/init ctx-static))

#_(defmethod kernel-eval :cljs [{:keys [id code]
                                 :or {id (guuid)}}]
    (let [c (chan)]
      (info "sci-eval: " code)
      (go (try (let [{:keys [error result]} (compile-code code)
                     eval-result (if error
                                   (merge {:id id} error)
                                   {:id id :picasso (->picasso result)})]
                 (>! c eval-result))
               (catch js/Error  e
                 (error "eval ex: " e)
                 (>! c {:id id
                        :error e})))
          (close! c))
      c))
