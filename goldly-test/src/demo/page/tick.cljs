(ns demo.page.tick
  (:require
   [clojure.string :as str]
   [tick.core :as t]
   [tick.goldly :refer [dt-format]]
   [layout]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn tick-page [_route]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-test"]
   [:p "blank? " (str (str/blank? "test"))]
   [:p "dt: " (str (t/now))]
   [:p "dt: " (dt-format "YYYY-MM-dd" (t/now))]
;   
   ])

(def tick-page
  (wrap-layout tick-page))
