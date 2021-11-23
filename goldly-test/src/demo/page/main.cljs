




(defn main-body []
  [:div
   [:h1.text-2xl.text-red-600.m-5 "goldly-test"]

   [:p "test if goldy extension system is working."]
   [:p "when the binary time is appearing, it is a good start."]

   ; menu
   [:div.bg-green-300

    [:div.grid.grid-cols-2
     [:a.m-2 {:href "/devtools/help"}  "devtools"]
     [:a.m-2 {:href "/scicompile"}  "scicompile"]
     [:a.m-2 {:href "/error"} "error"]
     [:a.m-2 {:href "/service"} "service-test"]
     [:a.m-2 {:href "/lazy"} "lazyload-test"]]]

   [:h1 "binary time"]
   [clock]])



(defn main-page [{:keys [handler route-params query-params] :as route}]

  [site/main-with-header
   ;[:div "header"]
   header  
   30
   [main-body]])

(add-page main-page :user/main)