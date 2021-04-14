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
    [:<>
     [:h1 "running systems: " (count @systems)]
     [:ul
      (for [{:keys [id]} @systems]
        ^{:key id}
        [:li.m-3
         [:a {:class "m-3 bg-yellow-300"
              :href (bidi/path-for (:client @routes) :ui/system :system-id id)} id]])]])) ; (str "/system/" id) "#/system/"

(defmethod reagent-page :ui/system-list [& args]
  [systems-list-page])




