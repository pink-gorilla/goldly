(ns goldly.plot
  (:require
   [pinkgorilla.ui.gorilla-plot.plot :as plot]
   [pinkgorilla.ui.gorilla-plot.core :refer [compose]]
   [pinkgorilla.ui.pinkie :as pinkie :refer [register-tag]]
   [pinkgorilla.ui.vega :refer [vega vegaa]]))

; performance tests

(defn xxx [state]
  [:p/leaflet
   (into [{:type :view :center [-16, 170.5] :zoom 4 :height 600 :width 700}]
         (for [{:keys [lat long]} (:quakes @state)]
           {:type :marker :position [lat long]}))])

(pinkie/register-tag :p/xxx xxx)

; todo: move gorilla plot

; this wrappers are necessary, as the repl api for gorilla-plot 
; supplies options as partitioned :key :val args in the end
; reagent syntax typically passes options as a map in the
; first parameter.


(defn listplot
  ([data]
   (listplot {} data))
  ([options data]
   [vega (apply plot/list-plot data (apply concat options))]))

(defn barchart
  ([cat val]
   (barchart {} cat val))
  ([options cat val]
   [vega (apply plot/bar-chart cat val (apply concat options))]))

; histogram not yet working. the gorillaplot implementation uses way too
; much Math/xx unctions that are not defined in clojurescript.
(defn histogram
  ([data]
   (histogram {} data))
  ([options data]
   [vega (apply plot/histogram data (apply concat options))]))

(defn plot
  ([fun min-max]
   (plot {} fun min-max))
  ([options fun min-max]
   [vega (apply plot/plot fun min-max (apply concat options))]))

(defn sin [x]
  (.sin js/Math x))


(println "registering vega dsl plots .. ")
(register-tag :p/listplot listplot)
(register-tag :p/barchart barchart)
(register-tag :p/histogram histogram)
(register-tag :p/plot plot)
(register-tag :p/composeplot compose) ; compose has problem with recursive pinkie.


