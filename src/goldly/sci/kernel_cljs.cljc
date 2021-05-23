(ns goldly.sci.kernel-cljs
  (:require
   #?(:clj  [clojure.core.async :refer [>! chan close! go]]
      :cljs [cljs.core.async :refer [>! chan close!] :refer-macros [go]])
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [picasso.id :refer [guuid]]
   [picasso.kernel.protocol :refer [kernel-eval]]
   [picasso.converter :refer [->picasso]]
   [sci.core :as sci]
   [goldly-bindings-generated :refer [bindings-generated]]
   [goldly.sci.bindings-static :refer [ns-static]]
   [sci.impl.vars]
   [picasso.protocols :refer [Renderable render]]
   [picasso.render.span :refer [span-render]]))

(def ctx (sci/init {:bindings bindings-generated
                    :preset {:termination-safe true}
                    :namespaces ns-static}))

(defn compile-code [code]
  (try
    {:result (sci/eval-string code ctx)}
    (catch :default e
      (error "sci compile-code --]" code "[-- ex: " e)
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