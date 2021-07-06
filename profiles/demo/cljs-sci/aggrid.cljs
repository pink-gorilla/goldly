(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])
(def data
  (map (fn [i] {:idx i :name "jon" :date (t/now) :text "jg kj jkhj jh gk g"}) (range 500) ))

(defmethod reagent-page :user/aggrid [{:keys [route-params query-params handler] :as route}]
  [:div.h-screen.w-screen
   [:div {:style {:position "absolute"}}
   [link-href "/" "main"]]
   ;[:p "data" (pr-str data)]
   [aggrid {:data data
               :box :fl ; :lg
               :pagination true
               :paginationAutoPageSize true}]
   ])
