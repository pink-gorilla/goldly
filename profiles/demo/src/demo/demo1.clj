(ns demo.demo1
  (:require
   [shiny.core :as shiny]
   [shiny.web :as shinyweb])
  (:gen-class))


(def s1
  (shiny/system
   {:state 0
    :html  [:div "Clicked "
            [:button {:on-click ?incr} @state]
            " times"]
    :fns {:incr (fn [_ s] (inc s))}}
   {:fns {:incr10  (fn [_ s] (+ s 10))}}))

(def s2
  (shiny/system
   {:state {:in "" :msg "Type Something..."}
    :html
    [:div.rows
     [:div [:input {:type "text" :on-change (?hello "Hello") :value (:in @state)}]]
     [:div (:msg @state)]]
    :fns {:hello
          (fn [e s prefix]
            (assoc s :in (:value e)
                   :msg (str prefix ", " (:value e))))}}
   {:fns {:incr10  (fn [_ s] (+ s 10))}}))



(def s3
  (shiny/system
   {:state "CommonLisp"
    :html  [:div "Favorite Language: " (:language @state)
            [:p/pselect ["Clojure" "Clojurescript" "Schema" "CommonLisp" "Elixir"]
             state]]
    :fns {:incr (fn [_ s] (inc s))}}
   {:fns {:incr10  (fn [_ s] (+ s 10))}}))


(defn -main []
  (shinyweb/server-start! {:port 8000})
  (shiny/system-start! "/demo1" s1)
  (shiny/system-start! "/demo2" s2)
  (shiny/system-start! "/demo3" s3)
 ; 
  )




