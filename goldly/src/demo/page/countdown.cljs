
(defn countdown-page [route-data]
  (let [state (r/atom 0)]
    (fn [route-data]
      (timeout #(swap! state inc) 1000)
      [:div
       "Seconds Elapsed: "
       @state])))

(add-page countdown-page :demo-countdown)