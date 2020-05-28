(ns systems.holiday-destinations
  (:require
   [goldly.core :as goldly]))

(def places
  {:london [{:type :view :center [51.49, -0.08] :zoom 12 :height 600 :width 700}
            {:type :rectangle :bounds [[51.49, -0.08]
                                       [51.5, -0.06]]}]
   :panama [{:type :view :center [9.1880621 -82.0075993] :zoom 12 :height 600 :width 700}
            #_{:type :rectangle :bounds [[51.49, -0.08]
                                         [51.5, -0.06]]}]})

(def holiday-destinations
  (goldly/system
   {:name "holiday destinations"
    :state :london
    :html [:<>
           [:p/pselect [:london :panama :atlantis] state]
           [:button {:class "border-solid border-blue-900"
                     :on-click ?getdestination } "get data"]
           [:p/leaflet
            [{:type :view :center [51.49, -0.08] :zoom 12 :height 600 :width 700}
             {:type :rectangle :bounds [[51.49, -0.08] [51.5, -0.06]]}]]]
    :fns {:incr (fn [_ s] (inc s))}}
   {:fns {:getdestination  (fn [_ s] (+ s 10))}}))



