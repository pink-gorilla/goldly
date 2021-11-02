(ns goldly.page.no-page
  (:require
   [webly.web.handler :refer [reagent-page]]))

(defn no-page []
  [:div.m-10.p-10.bg-blue-300.border.border-round.border-red-600
   [:p.text-2xl.text-red-800.mb-10 "Here should be your app!"]
   [:p "Your config needs to include [:goldly :routes] {:app {\"\"} :api {} }"]])

(defmethod reagent-page :goldly/no-page [{:keys [route-params query-params handler] :as route}]
  [no-page])
