(ns goldly-server.pages.no-app
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [ui.site.template :as t]
   [goldly-server.site :refer [header splash]]
   [goldly-server.helper.ui :refer [link-dispatch link-href]]))

(defn no-app []
  (let [routes (subscribe [:webly/routes])]
    (fn []
      [:div.m-10.p-10.bg-blue-300.border.border-round.border-red-600
       [:p.text-2xl.text-red-800.mb-10 "Here should be your app! (alt-g a)"]
       [:p
        [link-href "/goldly/about" "developer help (alt-g m)"]
        [link-dispatch [:bidi/goto :goldly/system :system-id :snippet-registry] "snippets (alt-g s)"]
        ;[link-href "/bongo" "bongo"]
        ;
        ]])))

(defmethod reagent-page :goldly/no-app [{:keys [route-params query-params handler] :as route}]
  [no-app])

