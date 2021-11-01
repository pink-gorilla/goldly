(ns goldly-server.pages.about
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [ui.site.template :as t]
   [goldly-server.helper.site :refer [header splash]]
   [goldly-server.helper.ui :refer [link-dispatch link-href]]))

(defn about []
  (let [routes (subscribe [:webly/routes])]
    (fn []
      (let [href-systems (bidi/path-for (:client routes) :goldly/system-list)]

        [:div
         [header]
         [splash]
         [t/cols-three {:title ["Use-cases"
                                [:br]
                                "just by using clojure"]
                        :link-text "Experienced team"
                        :link-href "#"
                        :cols [{:title "Dashboard" :text "Dashboards can load data from the server. Allow your users to change what they want to see"}
                               {:title "Notebook" :text "In a notebook you can use it for data exploration."}
                               {:title "Embedded" :text "Embed it to another website"}]}]

         [t/footer {:copyright "© 2019-2021 Pink Gorilla"
                    :right "Served by Goldly"}]]))))

(defmethod reagent-page :goldly/about [{:keys [route-params query-params handler] :as route}]
  [about])