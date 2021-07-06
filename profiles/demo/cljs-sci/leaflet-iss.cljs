
; ISS Location tracker - realtime

(def issstate
  (r/atom
   {:first true
    :data "not yet downloaded"}))


(defn pos [] ;{ :iss_position {:longitude "-111.4007", :latitude "2.9204"}}
  (when-let [lng (get-in @issstate [:data :iss_position :longitude])]
    (when-let [lat (get-in @issstate [:data :iss_position :latitude])]
      [(parse-float lat) (parse-float lng)])))

(defn download []
     ;; http://open-notify.org/Open-Notify-API/ISS-Location-Now/
  (info "downloading iss pos..")
  (get-json "http://api.open-notify.org/iss-now.json" issstate [:data]))

(defn iss []
  [:div.w-screen.h-screen
   (when (:first @issstate)
     (swap! issstate assoc :first false)
     (download)
     (interval download 30000)
     nil)
   [:p "raw data: " (pr-str (:data @issstate))]
   (when-let [p (pos)]
     [:<>
      [:p "location "  (pr-str p)]
      [leaflet {:box :fs ; :lg
                :center p
                   :zoom 4
                   :features [{:type :marker
                               :position p
                               :popup "the international space station is here!"}]}]])])

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/iss [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [iss]])