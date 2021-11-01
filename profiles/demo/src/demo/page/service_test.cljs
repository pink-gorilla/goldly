
(defn service-page []
  (let [state (r/atom {:first true})]
    (fn []
      [:div
        [:a.m-1 {:href "/"}  "main"]

       (when (:first @state)
         (swap! state assoc :first false)
         (run-a state [:add-result] :demo/add 2 7)
         (run-a state [:saying] :demo/quote)
         (run-a state [:ex-result] :demo/ex)
         nil)
       [:p "this tests if clj services are working"]
       [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]])))


(add-page service-page :user/service)