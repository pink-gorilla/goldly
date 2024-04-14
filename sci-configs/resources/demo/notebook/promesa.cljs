(ns demo.notebook.promesa
  (:require
   [reagent.core :as r]
   [promesa.core :as p]))

(defn fetch-uuid-v1
  []
  (let [d (r/atom "no data")]
    (p/let [response (js/fetch "/r/repl/bongo.json") ;"https://httpbin.org/uuid"
            data (.json response)]
      (.log js/console "data rcvd:" data)
      (reset! d data))
    (fn []
      [:div "data: " (pr-str @d)])))

(defn fetch-uuid-v2
  []
  (p/-> (js/fetch "https://httpbin.org/uuid") .json))

^:R
[fetch-uuid-v1]

;(fetch-uuid-v2)