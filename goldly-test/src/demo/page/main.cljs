(ns demo.page.main
  (:require
   [goldly.page :as page]
   [layout]
   [ui.clock :refer [clock]]
   [demo.cljs-libs.helper :refer [#_add-page-test test-header]]))

(defn main-body []
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-test"]
   [:p.m-2 "test if goldy extension system is working."]

   [:p.bg-green-300
    [:a.m-2 {:href "/devtools/help"}  "devtools"]]

   [:p "when the "
    [:span.text-red-500 "binary time "]
    "is appearing, it is a good start."]
   [:h1 "binary time"]
   [clock]
;   
   ])

(defn main-page [{:keys [_handler _route-params _query-params] :as _route}]
  [layout/header-main
   ;[:div "header"]
   [test-header]
   [main-body]])

(page/add main-page :user/main)
