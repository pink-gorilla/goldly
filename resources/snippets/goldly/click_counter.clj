{:state 42
 :html  [:div "Clicked "
           [:button {:class "border m-2 p-3 border-pink-500 text-xl"
                     :on-click ?incr} 
            @state]
           " times"]
  :fns {:incr (fn [_ s] (inc s))}}
