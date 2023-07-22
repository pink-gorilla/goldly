(ns page.service-test
  (:require
   [reagent.core :as r]
   [goldly.service.core :refer [run-a run-a-map]]
   [cljs-libs.helper :refer [add-page-test]]))

(defn service-page []
  (let [state (r/atom {:first true})]
    (fn []
      [:div
       [:p "this tests if clj services are working"]
       [:p "you should see one error message (for a service that is not defined)"]
       (when (:first @state)
         (swap! state assoc :first false)
         (run-a state [:add-result] :demo/add 2 7)
         (run-a state [:saying] :demo/quote)
         (run-a state [:ex-result] :demo/ex)
         (run-a-map {:a state
                     :path [:saying2]
                     :fun :demo/quote-slow
                     :timeout 1000})
         nil)

       [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]])))

(add-page-test service-page :user/service)
