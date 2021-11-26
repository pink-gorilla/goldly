(ns notebook.svg-sierpinsky)

;;; # Lets create a complex svg data structure

(def svg-data
  [:svg
   {:viewbox "-10 -10 116 111"}
   [:path
    {:d "M0,0  h7.75     a45.5,45.5 0 1 1 0,91     h-7.75     v-20     h7.75     a25.5,25.5 0 1 0 0,-51     h-7.75     z     m36.2510,0     h32     a27.75,27.75 0 0 1 21.331,45.5     a27.75,27.75 0 0 1 -21.331,45.5     h-32     a53.6895,53.6895 0 0 0 18.7464,-20     h13.2526     a7.75,7.75 0 1 0 0,-15.5     h-7.75     a53.6895,53.6895 0 0 0 0,-20     h7.75     a7.75,7.75 0 1 0 0,-15.5     h-13.2526     a53.6895,53.6895 0 0 0 -18.7464,-20     z   "
     :fill "#bbb"}]
   [:g
    {:stroke-opacity "0.3"
     :stroke-width "0.15"
     :stroke "currentColor"
     :fill "none"}
    [:line {:y2 "0", :y1 "0", :x2 "200", :x1 "-100"}]
    [:line {:y2 "20", :y1 "20", :x2 "200", :x1 "-100"}]
    [:line {:y2 "35.5", :y1 "35.5", :x2 "200", :x1 "-100"}]
    [:line {:y2 "45.5", :y1 "45.5", :x2 "200", :x1 "-100"}]
    [:line {:y2 "55.5", :y1 "55.5", :x2 "200", :x1 "-100"}]
    [:line {:y2 "71", :y1 "71", :x2 "200", :x1 "-100"}]
    [:line {:y2 "91", :y1 "91", :x2 "200", :x1 "-100"}]
    [:line {:y2 "200", :y1 "-100", :x2 "0", :x1 "0"}]
    [:line {:y2 "200", :y1 "-100", :x2 "7.75", :x1 "7.75"}]
    [:line {:y2 "200", :y1 "-100", :x2 "60.5", :x1 "60.5"}]

  ; XXX 
    [:line {:y2 "200", :y1 "-100", :x2 "68.25", :x1 "68.25"}]
    [:line {:y2 "200", :y1 "-100", :x2 "96", :x1 "96"}]]
   [:g
    {:fill "blue"}
    [:circle {:r ".5", :cy "45.5", :cx "7.75"}]
    [:circle {:r ".5", :cy "27.75", :cx "68.25"}]
    [:circle {:r ".5", :cy "63.25", :cx "68.25"}]]
   [:g
    {:fill "red"}
  ;; Intersecting horizontal lines with the r=53.6895 circle.
    [:circle {:r ".5", :cy "0", :cx "36.2510"}]
    [:circle {:r ".5", :cy "20", :cx "54.9974"}]
    [:circle {:r ".5", :cy "35.5", :cx "60.5"}]
    [:circle {:r ".5", :cy "55.5", :cx "60.5"}]
    [:circle {:r ".5", :cy "71", :cx "54.9974"}]
    [:circle {:r ".5", :cy "91", :cx "36.2510"}]
  ;; Intersecting the two r=27.75 circles. 
    [:circle {:r ".5", :cy "45.5", :cx "89.5807"}]]
   [:g
    {:stroke-width "0.15", :stroke "currentColor", :fill "none"}
    [:circle {:r "25.5", :cy "45.5", :cx "7.75"}]
    [:circle {:r "45.5", :cy "45.5", :cx "7.75"}]
  ;; Radius is computed to intersect at the intended x=60.5.
    [:circle {:r "53.6895", :cy "45.5", :cx "7.75"}]
    [:circle {:r "7.75", :cy "27.75", :cx "68.25"}]
    [:circle {:r "27.75", :cy "27.75", :cx "68.25"}]
    [:circle {:r "7.75", :cy "63.25", :cx "68.25"}]
    [:circle {:r "27.75", :cy "63.25", :cx "68.25"}]]])

svg-data

;;; # Create a canvas (to which we will paint to later in the notebook)

[:canvas#bongo {:width 500 :height 500}]

(defn mid-point [p1 p2]
  (map #(/ (+ % %2) 2) p1 p2))

(defn next-triangles [[top left right]]
  [[top (mid-point top left) (mid-point top right)]
   [(mid-point left top) left (mid-point left right)]
   [(mid-point right top) (mid-point left right) right]])

(def canvas (js/document.getElementById "bongo"))
(doto (.getContext canvas "2d")
  (aset "strokeStyle" "black")
  (.clearRect 0 0 (.-width canvas) (.-height canvas)))

(defn draw-triangle [canvas [[x1 y1] [x2 y2] [x3 y3]]]
  (doto (.getContext canvas "2d")
    (.beginPath)
    (.moveTo x1 y1)
    (.lineTo x2 y2)
    (.lineTo x3 y3)
    (.lineTo x1 y1)
    (.stroke)
    (.closePath)))

(defn draw-sierp [canvas initial n]
  (when (> n 0)
    (doseq [triangle (next-triangles initial)]
      (draw-triangle canvas triangle)
      (draw-sierp canvas triangle (dec n)))))

(draw-sierp canvas [[0 500] [250 0] [500 500]] 6)
