(ns page.service-test
  (:require
   [reagent.core :as r]
   [goldly.service :refer [run-a]]
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
         nil)

       [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]])))

(add-page-test service-page :user/service)