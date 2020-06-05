(ns systems.click-counter
  (:require
   [goldly.system :as goldly]))

(def click-counter
  (goldly/system
   {:name "click counter"
    :state 42
    :html  [:div "Clicked "
             [:button {:class "border m-2 p-3 border-pink-500"
                       :on-click ?incr} @state]
            " times"]
    :fns {:incr (fn [_ s] (inc s))}}
   {:fns {}}))


(comment

  (println click-counter)
  ;
  )