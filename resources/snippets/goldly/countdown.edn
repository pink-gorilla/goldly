
{:state 0
 :id :countdown
 :html  (fn []
          (timeout #(swap! state inc) 1000)
          [:div
           "Seconds Elapsed: " @state])
 :fns {:incr (fn [_ s] (inc s))}}

