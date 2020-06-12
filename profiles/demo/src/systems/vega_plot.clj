(ns systems.vega-plot
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]))

(def-ui d [1 3 5 7 9 5 4 6 9 8 3 5 6])

(def-ui hdata
  (into [] (repeatedly 1000 #(rand 10))))

;(def-ui hist
;(compose
  ;
  ;(plot (constantly 0.1) [0 10])
 ; )

;
#_(compose
   (list-plot (map #(vector % (rand %)) (range 0 10 0.01)) :opacity 0.3 :symbol-size 50)
   (plot (fn [x] (* x (Math/pow (Math/sin x) 2))) [0 10]))

(system-start!
 (goldly/system
  {:name "vega plot"
   :route "/vega-charts"
   :state {}
   :html [:<>
          [:h1 "Vega charts"]
          [:h4 "generated via gorilla-plot dsl"]
          [:div.flex.flex-row.content-between
           [:div.flex.flex-col.justify-start
            [:p/listplot d]
            [:p/listplot {:joined true
                          :plot-size 400
                          :color "red"
                          :aspect-ratio 1.6
                          :plot-range [:all :all]
                          :opacity 0.5} d]
            #_[:p/composeplot
               [:p/listplot d]
               [:p/listplot {:joined true
                             :color "blue"
                             :plot-range [1 5]} d]]
            #_[:p/histogram {:color "steelblue"
                             :bins 100
                             :normalize :probability-density} hdata]
            [:p/barchart (range (count d)) d]
            [:p/plot {:color "orange"
                      :plot-points 50}
             (fn [x] (sin x)) [0 10]]]]] ; sin is a symbol know to sci compiler
   :fns {;:incr (fn [_ s] (inc s))
         }}
  {:fns {;:getdestination [(fn [place] (place places)) [:map]]
         }}))



