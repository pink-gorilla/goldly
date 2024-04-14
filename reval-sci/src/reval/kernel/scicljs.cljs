(ns reval.kernel.scicljs
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [goldly :refer [error-view]]
   [goldly.sci :refer [compile-sci-async]]
   [reval.goldly.viz.data :refer [value->data]]))

(defn eval-cljs [on-evalresult {:keys [code _ns]}]
  (let [er-p (compile-sci-async code)]
    (-> er-p
        (.then
         (fn [er]
           (when [er] ; {:id :code :value :err :out :ns}
             (let [er-h (-> er
                            (dissoc :value)
                            (merge (value->data (:value er)))
                            (assoc :out (js->clj (:out er))))]
               ;(.log js/console "eval result cljs: " (pr-str er-h))
               (on-evalresult er-h)
               ;(reset! cljs-er er-h)
               ))))
        (.catch (fn [e]
                  (.log js/console "eval failed: " e)
                  (when-let [sci-err (goldly/exception->error e)]
                    ;(reset! cljs-er sci-err)
                    (on-evalresult {:code code :err-sci sci-err})))))))

