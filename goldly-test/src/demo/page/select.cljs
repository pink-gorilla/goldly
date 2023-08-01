(ns demo.page.select
  (:require
   [goldly.log :refer [info]]
   [goldly.js :refer [alert]]
   [goldly :refer [eventhandler]]
   [layout]
   [demo.cljs-libs.helper :refer [add-page-test]]))

(defn select [list _k v]
  (let [on-change (fn [v _e]
                    (info (str "selected: " v))
                    (alert (str "selected: " v)))]
    (into [:select {:value (str v)
                    :on-change (eventhandler on-change)}]
          (map (fn [o]
                 [:option {:value o}
                  (str o)])
               list))))

(defn select-page [_r]
  [:div
   [select [:clojure :python :csharp :c] :python]])

(add-page-test select-page :user/select)
