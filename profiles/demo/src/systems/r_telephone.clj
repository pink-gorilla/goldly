(ns systems.r-telephone
  (:require
   [goldly.core :as goldly]
   [pinkgorilla.ui.gorilla-renderable :refer [Renderable render]]
   [tech.ml.dataset :as dataset]
   [clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<- rdiv r** r- r* ->code]]
   [clojisr.v1.require :refer [require-r]]
   [pinkgorilla.clojisr.repl :refer [->svg r-doc pdf-off]]))

; ported from:
; https://shiny.rstudio.com/gallery/telephones-by-region.html

#_(defmacro defs
    [& bindings]
    {:pre [(even? (count bindings))]}
    `(do
       ~@(for [[sym init] (partition 2 bindings)]
           `(def ~sym ~init))))


(println "configuring clojisr ..")
(require-r '[base :as base :refer [$ <- $<-]]
           '[utils :as u]
           '[stats :as stats]
           '[graphics :as g]
           '[datasets :refer :all])
(base/options :width 120 :digits 7)
(base/set-seed 11228899)
(pdf-off)
(println "clojisr configuring finished!")


(def r-telephone
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

    (goldly/system
     {:name "r-telephone"
      :state {:x3 nil
              :big false
              :plot1 nil}
      :html  [:div "show: "
              [:button {:on-click (fn [_ & _] (?x3))} "x3"]
              [:p/pinkie (:x3 @state)]
              [:span "Supersize me?"
               [:p/checkbox state :big]]
              [:button {:on-click (fn [_ & _]
                                    (if (:big @state)
                                      (?plot1 450 500)
                                      (?plot1 250 300)))} "plot1"]
              (:plot1 @state)]
      :fns {:incr (fn [s] (inc s))}}
     {:fns {:x3    [(fn []
                      (render x3))
                    [:x3]]
            :plot1 [(fn [width height]
                      (->svg {:width width :height height}
                             #(g/barplot
                               (base/rev difference)
                               :xlab "Election years 1948 to 2008"
                               :ylab "Height difference in cm")))
                    [:plot1]]}})))
