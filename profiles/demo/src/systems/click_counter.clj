(ns systems.click-counter
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]))

(def-ui panther
  [:p "Pink panther is here!"])

(system-start!
 (goldly/system
  {:name "click counter"
   :state 42
   :html  [:div "Clicked "
           panther
           [:button {:class "border m-2 p-3 border-pink-500"
                     :on-click ?incr} @state]
           " times"]
   :fns {:incr (fn [_ s] (inc s))}}
  {:fns {}}))


(comment

  (println click-counter)
  ;
  )