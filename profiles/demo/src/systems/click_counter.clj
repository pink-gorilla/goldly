(ns systems.click-counter
  (:require
   [goldly.core :as goldly]))


(def click-counter
  (goldly/system
   {:name "click counter"
    :state 42
    :html  [:div "Clicked "
            [:p/button {:on-click ?incr} @state]
            " times"]
    :fns {:incr (fn [s] (inc s))}}
   {:fns {}}
   ))