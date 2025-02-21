(ns demo.page.main
  (:require
   [ui.binaryclock :refer [binary-clock]]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn main-page1 [_route]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-demo"]
   [:p.m-2 "test if goldly extension system is working."]
   
   [:p "when the "
    [:span.text-red-500 "binary time "]
    "is appearing, it is a good start."]
   [:h1 "binary time"]
   [binary-clock]
;   
   ])

(def main-page
  (wrap-layout main-page1))


