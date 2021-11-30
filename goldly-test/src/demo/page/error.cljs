

(defn error-page [r]
  [:div
   [throw-ex]])

(add-page-test error-page :user-error)