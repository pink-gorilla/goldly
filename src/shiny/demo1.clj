(ns shiny.demo1
  (:require
   [shiny.core :as shiny]
   [shiny.web :as shinyweb])
  (:gen-class))


(def s (shiny/system
        {:state 0
         :html  [:div "Clicked "
                 [:button {:on-click ?incr} @state]
                 " times"]
         :fns {:incr (fn [_ s] (inc s))}}
        {:fns {:incr10  (fn [_ s] (+ s 10))}}))

(defn -main []
  (shinyweb/server-start! {:port 8000})
  (shiny/system-start! "/demo1" s))




