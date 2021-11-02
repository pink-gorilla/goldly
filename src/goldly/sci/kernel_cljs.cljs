(ns goldly.sci.kernel-cljs
  (:require
   ;#?(:clj  [clojure.core.async :refer [>! chan close! go]]
      ;:cljs 
   [cljs.core.async :refer [>! chan close!] :refer-macros [go]]
           ; )
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [sci.core :as sci]
   [goldly.sci.sci-types]
  ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated]]
   ;[goldly.sci.lazy :refer [load-fn]]
   ))

(defn add-lazy [namespaces]
  (assoc namespaces
         'snippets
         {'add (sci/new-var 'add :internal)}))

(declare ctx-repl) ; since we want to add compile-sci to the bindings, we have to declare the ctx later

(defn compile-code [code]
  (try
    {:result (sci/eval-string code ctx-repl)}
    (catch :default e
      ;(error "sci compile-code --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(def ctx-static
  {:bindings (assoc bindings-generated 'compile-sci compile-code)
   :preset {:termination-safe true}
   :namespaces (add-lazy ns-generated) ; ns-static
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