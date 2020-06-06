(ns systems.r-telephone
  (:require
   [taoensso.timbre :as log :refer (tracef debugf info infof warnf error errorf)]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]
   [pinkgorilla.ui.gorilla-renderable :refer [Renderable render]]
   [tech.ml.dataset :as dataset]
   [clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<- rdiv r** r- r* ->code]]
   [clojisr.v1.require :refer [require-r]]
   [pinkgorilla.clojisr.repl :refer [->svg r-doc pdf-off]]
   [clojisr.v1.applications.plotting :refer
    [plot->svg plot->file plot->buffered-image]]))

; ported from:
; https://scicloj.github.io/clojisr/doc/clojisr/v1/tutorial-test/#data-visualization
; https://shiny.rstudio.com/gallery/telephones-by-region.html


; k means clustering
; https://shiny.rstudio.com/gallery/radiant.html


; nice dashboard - golf 
; https://shiny.rstudio.com/gallery/masters.html
; https://github.com/cjteeter/ShinyTeeter/blob/master/3_MastersGolf/app.R

(println "configuring clojisr ..")
(require-r '[base :as base :refer [$ <- $<-]]
           '[utils :as u]
           '[stats :as stats]
           '[graphics :as g :refer [plot hist]]
           '[datasets :refer :all]
           '[ggplot2 :refer [ggplot aes geom_point xlab ylab labs]])

(base/options :width 120 :digits 7)
(base/set-seed 11228899)
(pdf-off)
(println "clojisr configuring finished!")


(defn load-rds [filename]
  ; data <- attach(readRDS("data.rds"))
  [])

(defn gen-cum-data []
  (->> rand
       (repeatedly 30)
       (reductions +)))

(defn cum-plot []
  (->svg {:width 7 :height 5}
         (fn []
           (->> gen-cum-data
                (plot :xlab "t"
                      :ylab "y"
                      :type "l")))))

(defn dataset-plot []
  (->svg {:width 7 :height 5}
         (let [x (repeatedly 99 rand)
               y (map + x (repeatedly 99 rand))]
           (-> {:x x
                :y y}
               dataset/name-values-seq->dataset
               (ggplot (aes :x x
                            :y y
                            :color '(+ x y)
                            :size '(/ x y)))
               (r+ (geom_point) (xlab "x") (ylab "y"))))))

(defn hist-plot []
  (let [svg (->svg {:width 7 :height 5}
                   (fn []
                     (hist [1 1 1 1 2 3 4 5]
                           :main "Histogram"
                           :xlab "data: [1 1 1 1 2 3 4 5]")))]
    (info "svg: " svg)
    svg))

(def target-path "./")

(defn hist-plot-file []
  (println
   "r plot saved: "
   (r->clj
    (plot->file
     (str target-path "histogram.jpg")
     (fn []
       (hist [1 1 1 1 2 3 4 5]
             :main "Histogram"
             :xlab "data: [1 1 1 1 2 3 4 5]"))
     :width 800
     :height 400
     :quality 50))))

;(hist-plot-file)



(let [x [109.0 65.0 22.0 3.0 1.0]
      x1 (r->clj (r [109.0 65 22 3 1]))
      x2 (r->clj
          (r "c(109, 65, 22, 3, 1)"))
      x3 (r '(c 109.0 65.0 22.0 3.0 1.0))
      x4 (r->clj (base/c 109.0
                         65.0 22.0
                         3.0 1.0))
      x5 (r->clj (r x))
      winner   [185 182 182 188 188 188 185 185 177
                182 182 193 183 179 179 175]
      opponent [175 193 185 187 188 173 180 177 183
                185 180 180 182 178 178 173]
      difference (r- winner opponent)]

  (system-start!
   (goldly/system
    {:name "r-telephone"
     :state {:x3 nil
             :big false
             :plot1 nil}
     :html  [:div "show: "
             [:button {:on-click (fn [_ & _] (?x3))} "x3"]
             [:p/pinkie (:x3 @state)]
             [:div.grid.grid-cols-3.gap-4.w-full.h-full  ; show charts in a grid with 3 columns
              [:div
               [:span "Supersize me?"
                [:p/checkbox state :big]]
               [:button {:on-click (fn [_ & _]
                                     (if (:big @state)
                                       (?plot1 7 6)
                                       (?plot1 7 5)))} "plot1"]
               (:plot1 @state)]
              [:div
               [:button {:on-click (fn [_ & _] (?cum-plot))} "cum-plot"]
               (:cum-plot @state)]
              [:div
               [:button {:on-click (fn [_ & _] (?dataset-plot))} "dataset-plot"]
               (:dataset-plot @state)]
              [:div
               [:button {:on-click (fn [_ & _] (?hist-plot))} "histogram-plot"]
               (:hist-plot @state)]]]
     :fns {:incr (fn [s] (inc s))}}
    {:fns {:cum-plot [cum-plot [:cum-plot]]
           :dataset-plot [dataset-plot [:dataset-plot]]
           :hist-plot [hist-plot [:hist-plot]]
           :x3    [(fn []
                     (render x3))
                   [:x3]]
           :plot1 [(fn [width height]
                     (->svg {:width width :height height}
                            #(g/barplot
                              (base/rev difference)
                              :xlab "Election years 1948 to 2008"
                              :ylab "Height difference in cm")))
                   [:plot1]]}})))
