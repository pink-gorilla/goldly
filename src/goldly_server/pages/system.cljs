(ns goldly-server.pages.system
  (:require
   [taoensso.timbre :as timbre :refer-macros [info]]
   [webly.web.handler :refer [reagent-page]]
   [goldly.system :refer [system-ext]]
   [goldly-server.site :refer [header]]))

(defn systems-header [system id]
  [:h1.bg-orange-300 (str (:name @system) " " id)])

(defn system-themed [id ext]
  [:div
   [header]
   [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
    [:p.mt-5.mb-5.text-purple-600.text-3xl id]
    [system-ext id ext]]])

(defmethod reagent-page :goldly/system [{:keys [handler route-params query-params tag] :as route}]
  (info "loading system: " route)
  (let [system-id (or (:system-id route-params)
                      tag)]
    [system-themed system-id ""]))

(defmethod reagent-page :goldly/system-ext [{:keys [route-params query-params handler] :as route}]
  (info "loading system-ext" route-params)
  [system-themed (:system-id route-params) (:system-ext route-params)])