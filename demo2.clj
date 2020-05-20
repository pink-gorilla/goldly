{:state {:in "" :msg "Type Something..."} 
 :html 
   [:div.rows 
      [:div [:input {:type "text" :on-change (?hello "Hello") :value (:in ?state)}]] 
      [:div (:msg ?state)]] 
  :fns {:hello 
          (fn [e s prefix] 
            (assoc s :in (:value e) 
                      :msg (str prefix ", " (:value e)))
