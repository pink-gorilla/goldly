




(defn main-body []
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-test"]

   [:p "test if goldy extension system is working."]
   [:p "when the binary time is appearing, it is a good start."]

   [:h1 "binary time"]
   [clock]

   [:h1 "devtoools"]
   [:div.bg-green-300
    [:a.m-2 {:href "/devtools/help"}  "devtools"]]
;   
   ])

(defn main-page [{:keys [handler route-params query-params] :as route}]

  [layout/header-main
   ;[:div "header"]
   [test-header]
   [main-body]])

(add-page main-page :user/main)