(ns demo1
  (:require 
    [shiny]))


(def s (shiny/system 
  {:cljs {:state 0
          :html  [:div "Clicked " 
                   [:button {:on-click ?incr} ?state] 
                      " times"] 
          :fns {:incr (fn [_ s] (inc s))}}
   :clj {:fns {:incr10  (fn [_ s] (+ s 10)) }}}
  ))


(shiny/server-start! {:port 9000})
(shiny/system-start! "/demo1" s)




