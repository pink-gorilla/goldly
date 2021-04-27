(ns goldly.views.system-list
  (:require
   [taoensso.timbre :as timbre :refer-macros [info]]
   [re-frame.core :refer [subscribe reg-sub]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [goldly.views.site :refer [header]]
   ;[goldly.template :refer [site-template]]
   ))

(reg-sub
 :webly/routes
 (fn [db _]
   (get-in db [:bidi])))

(defn systems-list [routes systems]
  (into [:ul]
        (for [{:keys [id]} systems]
          ^{:key id}
          [:li.m-5
           [:a {:class "m-4 p-1 bg-yellow-300 border-round border-2 border-purple-500 hover:border-gray-500 shadow"
                :style {:font-family "Roboto script=latin rev=2"
                        :font-weight 400
                        :font-size 20
                        :font-style "normal"
                        :line-height 1.17188}
                :href (bidi/path-for (:client routes) :ui/system :system-id id)} id]])))

(defn systems-list-page []
  (let [routes (subscribe [:webly/routes])
        systems (subscribe [:systems])]
    (fn []
      ;[:div.bg-blue-200.h-screen.w-screen

      [:div
       [header]
       [:div.container.mx-auto
        [:h1.text-3xl "running systems: " (count @systems)]
        [systems-list @routes @systems]]])))

(defmethod reagent-page :goldly/system-list [{:keys [route-params query-params handler] :as route}]
  [systems-list-page])




