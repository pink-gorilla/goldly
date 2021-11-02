(ns goldly-server.pages.nrepl
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [webly.web.handler :refer [reagent-page]]
   [pinkgorilla.nrepl.view.info.page :as page]
   [goldly-server.helper.site :refer [header]]))

(defmethod reagent-page :nrepl/info [{:keys [route-params query-params handler] :as route}]
  [:div
   [header]
   [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
    [page/nrepl-info]]])