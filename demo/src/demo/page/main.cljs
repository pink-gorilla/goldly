

(defn main-page :user/main [{:keys [handler route-params query-params] :as route}]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "demo user app"]

   [:p "the real reason for this app is to test if goldy extension system is working."]
   [:p "when the binary time is appearing, it is a good start..."]

   ; menu
   [:div.bg-green-300

    [:a.m-2 {:href "/devtools/help"}  "devtools"]

    [:a.m-2 {:href "/scicompile"}  "scicompile"]
    [:a.m-2 {:href "/error"} "error"]
    [:a.m-2 {:href "/service"} "service-test"]
    [:a.m-2 {:href "/lazy"} "lazyload-test"]]

   [:h1 "binary time"]
   [clock]])

(add-page main-page :user/main)