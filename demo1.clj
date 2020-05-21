(ns demo1
  (:require 
    [shiny]))



(shiny/start! 
  {:state 0
   :html 
     [:div "Clicked " 
       [:button {:on-click ?incr} ?state] 
       " times"] 
   :fns {:incr (fn [_ s] (inc s))}}
  {:port 9000})






