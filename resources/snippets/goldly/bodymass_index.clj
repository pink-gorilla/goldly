(require '[taoensso.timbre :as timbre :refer [trace debug debugf info infof error]])
(require '[goldly.system :as goldly])
(require '[goldly.runner :refer [system-start!]])


; define the calculation function. Note that 
; 1. a change in the atom will trigger re-rendering
; 2. setting :bmi to nil will calculate bmi; otherwise the weight will be adjusted. 

; the slider is used for all 3 elements.
; however, the non-bmi sliders will reset :bmi property!

; lets change the height manually (the slider above adjusts)
; (swap! bmi-data assoc :height 150)
;(swap! bmi-data assoc :height 180)


(system-start!
 (goldly/system
{:id :bodymass-index
 :state {:height 180
         :weight 80}
 :html  (let [{:keys [weight height bmi]} (?calc-bmi)
              [color diagnose] (cond
                                 (< bmi 18.5) ["orange" "underweight"]
                                 (< bmi 25) ["inherit" "normal"]
                                 (< bmi 30) ["orange" "overweight"]
                                 :else ["red" "obese"])]
          [:div
           [:h3 "BMI calculator"]
           [:div
            "Height: " (int height) "cm"
            [?slider :height height 100 220]]
           [:div
            "Weight: " (int weight) "kg"
            [?slider :weight weight 30 150]]
           [:div
            "BMI: " (int bmi) " "
            [:span {:style {:color color}} diagnose]
            [?slider :bmi bmi 10 50]]])
 :fns {}
 :fns-raw {:calc-bmi (fn []
                       (let [{:keys [height weight bmi] :as data} @state
                             h (/ height 100)]
                         (if (nil? bmi)
                           (assoc data :bmi (/ weight (* h h)))
                           (assoc data :weight (* bmi h h)))))
           :slider (fn [param value min max]
                     [:input {:type "range" :value value :min min :max max
                              :style {:width "100%"}
                              :on-change (fn [e]
                                           (println "slider has changed!")
                                           (swap! state assoc param (evt-val e))
                                           (when (not= param :bmi)
                                             (swap! state assoc :bmi nil)))}])}}
 {:fns {}}
 ))

