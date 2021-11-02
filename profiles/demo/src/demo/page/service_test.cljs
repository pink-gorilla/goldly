
(defn service-page []
  (let [state (r/atom {:first true})]
    (fn []
      [:div
       [:a.m-1 {:href "/"}  "main"]
       [:p "this tests if clj services are working"]
       [:p "you should see one error message (for a service that is not defined)"]
       (when (:first @state)
         (swap! state assoc :first false)
         (run-a state [:add-result] :demo/add 2 7)
         (run-a state [:saying] :demo/quote)
         (run-a state [:ex-result] :demo/ex)
         nil)

       [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]])))

(add-page service-page :user/service)