(ns page.select
  (:require
   [goldly.log :refer [info]]
   [goldly.js :refer [alert]]
   [goldly :refer [eventhandler]]
   [layout]
   [clock]
   [cljs-libs.helper :refer [add-page-test]]))

(defn select [list k v]
  (let [on-change (fn [v e]
                    (info (str "selected: " v))
                    (alert (str "selected: " v)))]
    (into [:select {:value (str v)
                    :on-change (eventhandler on-change)}]
          (map (fn [o]
                 [:option {:value o}
                  (str o)])
               list))))

(defn select-page [r]
  [:div
   [select [:clojure :python :csharp :c] :python]])

(add-page-test select-page :user/select)