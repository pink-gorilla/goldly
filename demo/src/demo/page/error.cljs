

(defn error-page [r]
  [:div
   [:a.m-1 {:href "/"}  "main"]
   [throw-ex]])

(add-page error-page :user-error)