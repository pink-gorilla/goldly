(ns demo.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [webly.web.handler :refer [reagent-page]]))
  

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn main []
[:div
   [:h1 "goldly demo"]
 
   [:p [link-dispatch [:bidi/goto :ui/system-list] "goldly running systems"]]
   [:p [link-dispatch [:bidi/goto :ui/markdown :file "webly.md"] "webly docs"]]
   [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]


   ])

(defmethod reagent-page :demo/main [& args]
  [main])