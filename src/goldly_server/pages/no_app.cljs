(ns goldly-server.pages.no-app
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [ui.site.template :as t]
   [goldly-server.site :refer [header splash]]))

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-5.p-3
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  [link-fn #(dispatch rf-evt) text])

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-5.p-3
   {:href href} text])

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

