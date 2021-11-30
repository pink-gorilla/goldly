
(defn select [list k v]
  (let [on-change (fn [v e]
                    (info (str "selected: " v))
                    (alert (str "selected: " v)))]
    (into [:select {:value (str v)
                    :on-change (goldly/eventhandler on-change)}]
          (map (fn [o]
                 [:option {:value o}
                  (str o)])
               list))))

(defn select-page [r]
  [:div
   [select [:clojure :python :csharp :c] :python]])

(add-page-test select-page :user/select)