(ns goldly.web.views
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf errorf info]]
   [re-frame.core :refer [subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [goldly.web.routes :refer [goldly-routes-frontend]]))

(defn main-page []
  (let [systems (subscribe [:systems])
        _ (info "main-page showing: systems: " @systems)]
    [:<>
     [:h1 "running systems: " (count @systems)]
     [:ul
      (for [{:keys [id name]} @systems]
        ^{:key id}
        [:li.m-3
         [:a {:class "m-3 bg-yellow-300"
              :href (bidi/path-for goldly-routes-frontend :ui/system :system-id id)} name]])]])) ; (str "/system/" id) "#/system/"

(defmethod reagent-page :ui/system-list [& args]
  [main-page])





