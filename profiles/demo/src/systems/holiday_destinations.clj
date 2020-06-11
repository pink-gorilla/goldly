(ns systems.holiday-destinations
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

(def places
  {:london [{:type :view :center [51.49, -0.08] :zoom 12 :height 600 :width 700}
            {:type :rectangle :bounds [[51.49, -0.08]
                                       [51.5, -0.06]]}]
   :panama [{:type :view :center [9.1880621 -82.0075993] :zoom 12 :height 600 :width 700}
            #_{:type :rectangle :bounds [[51.49, -0.08]
                                         [51.5, -0.06]]}]})

(system-start!
 (goldly/system
  {:name "holiday destinations"
   :route "/holiday-destinaions"
   :state {:place :london
           :map [{:type :view :center [51.49, -0.08] :zoom 12 :height 600 :width 700}
                 {:type :rectangle :bounds [[51.49, -0.08] [51.5, -0.06]]}]}
   :html [:<>
          [:div {:class "flex flex-row content-between"}
           [:p/pselectm
            {:on-change ?getdestination}
            [:london :panama :atlantis] state :place]
           [:p/button {:on-click ?lucky} "Feeling Lucky!"]]
          [:p (str "map data: " (:map @state))]
          [:p/leaflet (:map @state)]]
   :fns {:incr (fn [_ s] (inc s))}}
  {:fns {:getdestination [(fn [place] (place places))
                          [:map]]
         :lucky [(fn [] (let [ks (into [] (keys places))
                              i (rand-int (count ks))
                              place (get ks i)]
                          (place places)))
                 [:map]]}}))



