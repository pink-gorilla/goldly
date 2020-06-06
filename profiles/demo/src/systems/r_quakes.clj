(ns systems.r-quakes
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]
   [pinkgorilla.ui.macros] ; force loading of gorilla-ui
   [pinkgorilla.ui.gorilla-renderable :refer [Renderable render]]
   [tech.ml.dataset :as ds]
   [clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<- rdiv r** r- r* ->code]]
   [clojisr.v1.require :refer [require-r]]))

(defmacro defs
  [& bindings]
  {:pre [(even? (count bindings))]}
  `(do
     ~@(for [[sym init] (partition 2 bindings)]
         `(def ~sym ~init))))

; ported from:
; https://rstudio.github.io/crosstalk/

(println "configuring clojisr ..")
(require-r '[base :as base :refer [$ <- $<-]]
           '[utils :as u]
           '[stats :as stats]
           '[graphics :as g :refer [plot hist]]
           '[datasets :refer :all]
           '[ggplot2 :refer [ggplot aes geom_point xlab ylab labs]])

(base/options :width 120 :digits 7)
(base/set-seed 11228899)
(println "clojisr configuring finished!")

(println "loading quake data..")
(require-r '[DT]
           '[dplyr])
;; Load dataset
(r '(data quakes))
; https://www.rdocumentation.org/packages/datasets/versions/3.6.2/topics/quakes

(defs
  quakes-ds (r/r->clj quakes)
  quakes-clj (ds/->flyweight quakes-ds))

(println "quakes all:" (count quakes-clj))

(println "quake data loaded!")

;; todo: ad start / stop events
;; To discard the current session running the Shiny app:
;; (r/discard-default-session)


(defn load-quakes-r [rmin rmax]
  ; sd <- SharedData$new (quakes [sample (nrow (quakes), 100)]) 
  (-> 'quakes
      (r.dplyr/filter `(& (>= mag ~rmin)
                          (<= mag ~rmax)))))

(defn p-mag [rmin rmax]
  (fn [{:keys [mag]}]
    (and (>= mag rmin) (<= mag rmax))))

(defn load-quakes-clj [rmin rmax]
  (println "loading quakes min:" rmin "max:" rmax)
  ; quake columns [:lat :long :depth :mag :stations]
  ; quake is a tech.ml dataset; clojisr converts R dataframes hat way.
  ; (println quakes)
  ;(println (ds/descriptive-stats quakes))
  (let [quakes-filtered (into [] (filter (p-mag rmin rmax) quakes-clj))]
    (println "quakes all:" (count quakes-clj)
             "filtered:" (count quakes-filtered))
    quakes-filtered))



(system-start!
 (goldly/system
  {:name "r earthquakes fiji"
   :state {:quakes [{:lat -22.55, :long 185.9, :stations 76, :depth 42, :mag 5.7}
                    {:lat -23.34, :long 184.5, :stations 106, :depth 56, :mag 5.7}
                    {:lat -26, :long 182.12, :stations 98, :depth 205, :mag 5.6}]
           :min 5.6
           :max 5.8}
   :html  [:div.rows
           [:h1.text-green-700 "Earthquakes in Fiji"]
           [:p/slideriona {:min 4.0 :max 5.7 :step 0.1} state :min :max]
           [:p/button {:on-click #(?load-quakes (:min @state) (:max @state))} "Load!"]
            ; filter_slider ("mag", "Magnitude", sd, column= ~mag, step=0.1, width=250)
           [:div.flex.w-full.h-20
            [:p/sparklinebar {:limit 300
                              :svgWidth 1200
                              :svgHeight 20
                              :width 1500
                              :height 20
                              :margin 1
                              :data (map :mag (:quakes @state))}]]
            ; :width 100 :height 20 :svgWidth 300 :svgHeight 20 :margin 5
           [:div.flex.flex-column.w-full.h-full
            #_[:p/xxx state]
            [:p/leaflet
             (into [{:type :view :center [-16, 170.5] :zoom 4 :height 600 :width 700}]
                   (for [{:keys [lat long]} (:quakes @state)]
                     {:type :marker :position [lat long]}))]
            [:div.h-100.w-full.ml-5
              ;[:h1 "data"]
              ;[:div (pr-str @state)]
             [:div {:className "ag-theme-balham"
                    :style {:height "100%" ;"400px" ; either both pixels, or both percentage.
                            :width "100%" ; "600px"
                            :color "blue"}}
              [:p/aggrid {:columnDefs  [{:headerName "R" :field "mag" :width 60 :sortable true}
                                        {:headerName "m" :field "depth" :width 70 :sortable true}
                                        {:headerName "#" :field "stations" :width 60 :sortable true}
                                        {:headerName "lat" :field "lat" :width 100 :sortable true}
                                        {:headerName "lng" :field "long" :width 100 :sortable true}]
                          :rowData (:quakes @state)
                          :pagination true
                          :paginationAutoPageSize true}]]]]]
   :fns   {}}
  {:fns {:load-quakes [load-quakes-clj [:quakes]]}}))