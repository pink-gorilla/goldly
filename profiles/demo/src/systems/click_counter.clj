(ns systems.click-counter
  (:require
   [goldly.core :as goldly]))


(def click-counter
  (goldly/system
   {:name "click counter"
    :state 0
    :html  [:div "Clicked "
            [:button {:class "border m-2 p-3 border-green-500"
                      :on-click ?incr} @state]
            " times"]
    :fns {:incr (fn [_ s] (inc s))}}
   {:fns {}}
   ))