{:state 0
  :html 
     [:div "Clicked " 
       [:button {:on-click ?incr} ?state] 
       " times"] 
   :fns {:incr (fn [_ s] (inc s))}}




