(ns goldly.puppet.views
  (:require
   [taoensso.timbre :as timbre :refer-macros [info]]
   [re-frame.core :refer [subscribe reg-sub]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]))

(reg-sub
 :webly/routes
 (fn [db _]
   (get-in db [:bidi])))

(defn systems-list-page []
  (let [routes (subscribe [:webly/routes])
        systems (subscribe [:systems])
        _ (info "main-page showing: systems: " @systems)]
    [:div.bg-blue-200.h-screen
     [:h1 "running systems: " (count @systems)]
     [:ul
      (for [{:keys [id]} @systems]
        ^{:key id}
        [:li.m-5
         [:a {:class "m-4 p-1 bg-yellow-300 border-round border-2 border-purple-500 hover:border-gray-500 shadow"
              :style {:font-family "Roboto script=latin rev=2"
                      :font-weight 400
                      :font-size 20
                      :font-style "normal"
                      :line-height 1.17188}
              :href (bidi/path-for (:client @routes) :ui/system :system-id id)} id]])]])) ; (str "/system/" id) "#/system/"

(defmethod reagent-page :ui/system-list [{:keys [route-params query-params handler] :as route}]
  [systems-list-page])




