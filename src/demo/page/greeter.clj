
(ns demo.greeter)

(defn greeter-page []
  (let [state (r/atom {:in ""
                       :msg "Type Something..."})
        change-state (fn [s e]
                       (swap! state
                              (assoc s :in (:value e)
                                     :msg (str prefix ", " (:value e)))))]
    (fn []
      [:div.rows
       [:input {:class "border border-blue-300"
                :type "text"
                :on-change #(change-state % "Hello")
                :value (:in @state)}]
       [:a {:href (str "/system/greeter-details/" (:in @state))}
        [:p.m-2.p-1.border.border-round (str "goto person: " (:in @state))]]
       [:div.text-2xl (:msg @state)]])))

(add-page greeter-page :greeter)



