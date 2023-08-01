(ns demo.page.tick
  (:require
   [clojure.string :as str]
   [tick.core :as t]
   [tick.goldly :refer [dt-format]]
   [goldly.page :as page]
   [layout]
   [demo.cljs-libs.helper :refer [#_add-page-test test-header]]))

(defn tick-body []
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-test"]
   [:p "blank? " (str (str/blank? "test"))]
   [:p "dt: " (str (t/now))]
   [:p "dt: " (dt-format "YYYY-MM-dd" (t/now))]
;   
   ])

(defn tick-page [{:keys [_handler _route-params _query-params] :as _route}]
  [layout/header-main
   ;[:div "header"]
   [test-header]
   [tick-body]])

(page/add tick-page :user/tick)
