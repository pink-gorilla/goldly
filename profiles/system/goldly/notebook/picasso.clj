(ns goldly.notebook.picasso
  (:require
   [picasso.protocols :refer [make render Renderable]]
   [goldly.runner])
  (:import [goldly.runner GoldlySystem]))

(extend-type GoldlySystem
  Renderable
  (render [{:keys [id]}]
    (make
     :pinkie
     [:p/goldly id])))
