
; main page 
(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/main [{:keys [handler route-params query-params] :as route}]
  [:div
   [:h1.text-2xl.text-red-600.m-5 "demo user app"]
   [link-href "/goldly/about" "goldly developer tools"]

   [:div.bg.green-300
      [link-href "/artefacts" "artefacts"]
      [link-href "/about" "goldly about"]
    
    ]
  
   [link-href "/vega" "vega"]
   [link-href "/bmi" "bmi"]
   [link-href "/fortune" "fortune cookies"]
   [link-href "/iss" "iss leaflet"]
   [link-href "/aggrid" "aggrid"]])