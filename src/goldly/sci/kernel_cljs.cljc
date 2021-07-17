(ns goldly.sci.kernel-cljs
  (:require
   #?(:clj  [clojure.core.async :refer [>! chan close! go]]
      :cljs [cljs.core.async :refer [>! chan close!] :refer-macros [go]])
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [picasso.id :refer [guuid]]
   [picasso.kernel.protocol :refer [kernel-eval]]
   [picasso.converter :refer [->picasso]]
   [picasso.protocols :refer [Renderable render]]
   [picasso.render.span :refer [span-render]]
   [sci.core :as sci]
   [sci.impl.vars]
   [goldly-bindings-generated :refer [bindings-generated ns-generated]]
   ;[goldly.sci.bindings-static :refer [ns-static]]
   ;[goldly.sci.lazy :refer [load-fn]]
   ))

(defn add-lazy [namespaces]
  (assoc namespaces
         'snippets
         {'add (sci/new-var 'add :internal)}))

(def ctx-static
  {:bindings bindings-generated
   :preset {:termination-safe true}
   :namespaces (add-lazy ns-generated) ; ns-static
   ;:load-fn load-fn
   })

(def ctx-repl (sci/init ctx-static))

(defn compile-code [code]
  (try
    {:result (sci/eval-string code ctx-repl)}
    (catch :default e
      ;(error "sci compile-code --]" code "[-- ex: " e)
      {:error #?(:clj e
                 :cljs {:root-ex (.-data e)
                        :err (.-message e)})})))

(defmethod kernel-eval :cljs [{:keys [id code]
                               :or {id (guuid)}}]
  (let [c (chan)]
    (info "sci-eval: " code)
    (go (try (let [{:keys [error result]} (compile-code code)
                   eval-result (if error
                                 (merge {:id id} error)
                                 {:id id :picasso (->picasso result)})]
               (>! c eval-result))
             (catch #?(:cljs js/Error :clj Exception) e
               (error "eval ex: " e)
               (>! c {:id id
                      :error e})))
        (close! c))
    c))

(extend-type sci.impl.vars/SciVar
  Renderable
  (render [self]
    (span-render self "clj-symbol")))

(extend-type sci.impl.vars.SciNamespace
  Renderable
  (render [self]
    (span-render self "clj-namespace")))