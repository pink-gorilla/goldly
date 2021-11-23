
(defn select [list k v]
  (let [on-change (fn [v e]
                    (info "selected: " v)
                    (alert (str "selected: " v)))]
    (into [:select {:value (str v)
                    :on-change (goldly/eventhandler on-change)}]
          (map (fn [o]
                 [:option {:value o}
                  (str o)])
               list))))

(defn select-page [r]
  [:div
   [:a.m-1 {:href "/"}  "main"]

   [select [:clojure :python :csharp :c] :python]])

(add-page select-page :user-select)