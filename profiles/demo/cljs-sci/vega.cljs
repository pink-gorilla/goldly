(def s
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json", 
   :data {:url "/r/demo/movies.json"}, 
   :transform [{:filter {:and [{:field "IMDB Rating", :valid true} 
                               {:field "Rotten Tomatoes Rating", :valid true}]}}],
   :mark "rect", 
   :width 300, 
   :height 200, 
   :encoding {:x {:bin {:maxbins 60},
                  :field "IMDB Rating", 
                  :type "quantitative"},
              :y {:bin {:maxbins 40},
                  :field "Rotten Tomatoes Rating",
                  :type "quantitative"}, 
              :color {:aggregate "count", :type "quantitative"}}, 
   :config {:view {:stroke "transparent"}}})

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/vega [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "vega..."]
   [:div "spec: " (pr-str {:spec s})]
   [vegalite {:box :sm
              :spec s}]
   ])
