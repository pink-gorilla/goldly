;; gorilla-repl.fileformat = 2

;; @@ [meta]
{:name "cljs-svg complex", :tagline "complex svg plot and quil example", :tags "cljs, svg, quil, cool, sierpinsky"}

;; @@

;; **
;;; # CLJS - SVG
;;; 
;;; Pure CLJS Notebook.
;;; Pretty Complex Drawings with SVG (including sierpinsky)
;; **

;; @@ [cljs]
(pinkgorilla.notebook.repl/r! [:h1 "svg data in cljs"])
;; @@
;; =>
;;; ["^ ","~:type","~:reagent","~:content",["^ ","~:hiccup",["~:h1","svg data in cljs"],"~:map-keywords",true]]
;; <=

;; **
;;; # Lets create a complex svg data structure
;; **

;; @@ [cljs]
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
  [:circle {:r "27.75", :cy "63.25", :cx "68.25"}]]] )  
  

 

;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'cljs.user/svg-data"],"~:value","#'cljs.user/svg-data"]
;; <=

;; @@ [cljs]
(pinkgorilla.notebook.repl/r! svg-data)
;; @@
;; =>
;;; ["^ ","~:type","~:reagent","~:content",["^ ","~:hiccup",["~:svg",["^ ","~:viewbox","-10 -10 116 111"],["~:path",["^ ","~:d","M0,0  h7.75     a45.5,45.5 0 1 1 0,91     h-7.75     v-20     h7.75     a25.5,25.5 0 1 0 0,-51     h-7.75     z     m36.2510,0     h32     a27.75,27.75 0 0 1 21.331,45.5     a27.75,27.75 0 0 1 -21.331,45.5     h-32     a53.6895,53.6895 0 0 0 18.7464,-20     h13.2526     a7.75,7.75 0 1 0 0,-15.5     h-7.75     a53.6895,53.6895 0 0 0 0,-20     h7.75     a7.75,7.75 0 1 0 0,-15.5     h-13.2526     a53.6895,53.6895 0 0 0 -18.7464,-20     z   ","~:fill","#bbb"]],["~:g",["^ ","~:stroke-opacity","0.3","~:stroke-width","0.15","~:stroke","currentColor","^7","none"],["~:line",["^ ","~:y2","0","~:y1","0","~:x2","200","~:x1","-100"]],["^;",["^ ","^<","20","^=","20","^>","200","^?","-100"]],["^;",["^ ","^<","35.5","^=","35.5","^>","200","^?","-100"]],["^;",["^ ","^<","45.5","^=","45.5","^>","200","^?","-100"]],["^;",["^ ","^<","55.5","^=","55.5","^>","200","^?","-100"]],["^;",["^ ","^<","71","^=","71","^>","200","^?","-100"]],["^;",["^ ","^<","91","^=","91","^>","200","^?","-100"]],["^;",["^ ","^<","200","^=","-100","^>","0","^?","0"]],["^;",["^ ","^<","200","^=","-100","^>","7.75","^?","7.75"]],["^;",["^ ","^<","200","^=","-100","^>","60.5","^?","60.5"]],["^;",["^ ","^<","200","^=","-100","^>","68.25","^?","68.25"]],["^;",["^ ","^<","200","^=","-100","^>","96","^?","96"]]],["~:g",["^ ","^7","blue"],["~:circle",["^ ","~:r",".5","~:cy","45.5","~:cx","7.75"]],["^@",["^ ","~:r",".5","^A","27.75","^B","68.25"]],["^@",["^ ","~:r",".5","^A","63.25","^B","68.25"]]],["~:g",["^ ","^7","red"],["^@",["^ ","~:r",".5","^A","0","^B","36.2510"]],["^@",["^ ","~:r",".5","^A","20","^B","54.9974"]],["^@",["^ ","~:r",".5","^A","35.5","^B","60.5"]],["^@",["^ ","~:r",".5","^A","55.5","^B","60.5"]],["^@",["^ ","~:r",".5","^A","71","^B","54.9974"]],["^@",["^ ","~:r",".5","^A","91","^B","36.2510"]],["^@",["^ ","~:r",".5","^A","45.5","^B","89.5807"]]],["~:g",["^ ","^9","0.15","^:","currentColor","^7","none"],["^@",["^ ","~:r","25.5","^A","45.5","^B","7.75"]],["^@",["^ ","~:r","45.5","^A","45.5","^B","7.75"]],["^@",["^ ","~:r","53.6895","^A","45.5","^B","7.75"]],["^@",["^ ","~:r","7.75","^A","27.75","^B","68.25"]],["^@",["^ ","~:r","27.75","^A","27.75","^B","68.25"]],["^@",["^ ","~:r","7.75","^A","63.25","^B","68.25"]],["^@",["^ ","~:r","27.75","^A","63.25","^B","68.25"]]]],"~:map-keywords",true]]
;; <=

;; **
;;; # Create a canvas (to which we will paint to later in the notebook)
;; **

;; @@ [cljs]
(pinkgorilla.notebook.repl/r! [:canvas#bongo {:width 500 :height 500} ])
;; @@
;; =>
;;; ["^ ","~:type","~:reagent","~:content",["^ ","~:hiccup",["~:canvas#bongo",["^ ","~:width",500,"~:height",500]],"~:map-keywords",true]]
;; <=

;; @@ [cljs]
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

;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-nil"],"nil"],"~:value","nil"]
;; <=

;; **
;;; # Let's use quil library
;; **

;; @@ [clj]
; QUIL demo

(ns qil-demo 
  (:require 
    [reagent.core :as reagent :refer [atom]] 
    [quil.core :as q :include-macros true] 
    [quil.middleware :as m])) 

(def w 400) 
(def h 400) 
(defn setup [] {:t 1}) 
(defn update [state] (update-in state [:t] inc)) 
(defn draw [state] 
  (q/background 255) 
  (q/fill 0) 
  (q/ellipse (rem (:t state) w) 46 55 55)) 

(q/defsketch foo :setup setup :update update :draw draw :host "foo" :no-start true :middleware [m/fun-mode] :size [w h]) 

(defn hello-world [] 
  (reagent/create-class 
    {:reagent-render (fn [] [:canvas#foo {:width w :height h}]) 
     :component-did-mount foo})) 

(reagent/render-component [hello-world] (. js/document (getElementById "app")))

;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-var"],"#'qil-demo/update"],"~:value","#'qil-demo/update"]
;; <=

;; **
;;; - https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/canvas-fills-div
;;; - http://lambdafunk.com/2017-03-30-Interactive-Sierpinski-Triangle/
;;; - https://github.com/stathissideris/dali
;;; - https://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;;; - https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/canvas-fills-div
;;; - https://wiki.sagemath.org/interact/fractal
;;; - http://lambdafunk.com/2017-02-16-Random-Polygons/
;; **
