(ns demo.page.scipage
  (:require
   [demo.cljs-libs.helper :refer [wrap-layout]]))


(defn sci-page1 [_r]
  [:div
   [:p "I am interpreted by sci"]
   [:p "1 + 2 = " (+ 1 2)]
   ])

(def sci-page
  (wrap-layout sci-page1))
