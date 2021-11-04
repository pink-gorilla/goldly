




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

;;; - https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/canvas-fills-div
;;; - http://lambdafunk.com/2017-03-30-Interactive-Sierpinski-Triangle/
;;; - https://github.com/stathissideris/dali
;;; - https://stackoverflow.com/questions/33345084/quil-sketch-on-a-reagent-canvas
;;; - https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/canvas-fills-div
;;; - https://wiki.sagemath.org/interact/fractal
;;; - http://lambdafunk.com/2017-02-16-Random-Polygons/
;; **
