(ns demo.page.service-test
  (:require
   [reagent.core :as r]
   [goldly.service.core :refer [run-a run-a-map]]
   [demo.cljs-libs.helper :refer [add-page-test]]))

(defn make-requests [state]
  (swap! state assoc :first false)
  (run-a state [:add-result]  'demo.service/add 2 7)
  (run-a state [:saying] 'demo.service/quote)
  (run-a state [:ex-result] 'demo.service/ex)
  (run-a-map {:a state
              :path [:saying2]
              :fun 'demo.service/quote-slow
              :timeout 1000})
  nil)

(defn show-page [state]
  [:div
   [:p "this tests if clj services are working"]
   [:p "you should see one error message (for a service that is not defined)"]
   (when (:first @state)
     (make-requests state))
   [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]])

(defn service-page []
  (let [state (r/atom {:first true})]
    [show-page state]))

(add-page-test service-page :user/service)
