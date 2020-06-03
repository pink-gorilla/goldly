(ns systems.r-quakes
  (:require
   [goldly.core :as goldly]
   [pinkgorilla.ui.macros] ; force loading of gorilla-ui
   [pinkgorilla.ui.gorilla-renderable :refer [Renderable render]]
   [tech.ml.dataset :as ds]
   [clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<- rdiv r** r- r* ->code]]
   [clojisr.v1.require :refer [require-r]]))

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
  (let [quakes-ds (r/r->clj quakes)
        quakes-clj (ds/->flyweight quakes-ds)
        _ (println "quakes all:" (count quakes-clj))
        quakes-filtered (into [] (filter (p-mag rmin rmax) quakes-clj))]
    (println "quakes all:" (count quakes-clj)
             "filtered:" (count quakes-filtered))
    quakes-filtered))

(def r-quakes
  (goldly/system
   {:name "r earthquakes fiji"
    :state {:quakes [{:lat -22.55, :long 185.9, :stations 76, :depth 42, :mag 5.7}
                     {:lat -23.34, :long 184.5, :stations 106, :depth 56, :mag 5.7}
                     {:lat -26, :long 182.12, :stations 98, :depth 205, :mag 5.6}]
            :min 5.6
            :max 5.8}
    :html  [:div.rows
            [:h1 "Earthquakes in fiji"]
            [:p/button {:on-click #(?load-quakes (:min @state) (:max @state))} "Load!"]
            ; filter_slider ("mag", "Magnitude", sd, column= ~mag, step=0.1, width=250)
            [:div.flex.flex-column
             [:p/leaflet
              (into [{:type :view :center [-16, 170.5] :zoom 4 :height 600 :width 700}]
                    (for [{:keys [lat long]} (:quakes @state)]
                      {:type :marker :position [lat long]}))]
             [:div
              [:h1 "data"]
              [:div (pr-str @state)]]]
            ;datatable (sd, extensions= "Scroller", style= "bootstrap", class= "compact", width= "100%"
            ;               options=list (deferRender=TRUE, scrollY=300, scroller=TRUE)) 
            ]
    :fns   {}}
   {:fns {:load-quakes [load-quakes-clj [:quakes]]}}))