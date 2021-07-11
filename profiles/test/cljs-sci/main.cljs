
; main page 

(defmethod reagent-page :user/main [{:keys [handler route-params query-params] :as route}]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "demo user app"]
   [link/href "/goldly/about" "goldly developer tools"]

   [:div.bg.green-300
    [link/href "/info" "info"]
    [link/href "/error" "error"]]])