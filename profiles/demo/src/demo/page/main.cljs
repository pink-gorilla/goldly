

(defn main-page :user/main [{:keys [handler route-params query-params] :as route}]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "demo user app"]

   [:p "the real reason for this app is to test if goldy extension system is working."]
   [:p "when the binary time is appearing, it is a good start..."]

   [:div.bg-green-300
    [:a.m-1 {:href "/info"}  "info"]
    [:a.m-1 {:href "/error"} "error"]
    [:a.m-1 {:href "/service"} "service test"]]

   [:h1 "binary time"]
   [clock]])

(add-page main-page :user/main)