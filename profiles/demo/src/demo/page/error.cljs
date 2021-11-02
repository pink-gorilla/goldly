

(defn error-c [r]
  [:div
   [:a.m-1 {:href "/"}  "main"]
   [throw-ex]])

(add-page error-c :user/error)