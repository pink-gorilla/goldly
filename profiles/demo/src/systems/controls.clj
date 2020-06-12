(ns systems.controls
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

(system-start!
 (goldly/system
  {:name "controls"
   :route "/controls"
   :state {:language "CommonLisp"}
   :html  [:div
           [:p "Favorite Language: " (:language @state)]
           [:p/pselectm ["Clojure" "Clojurescript" "Schema" "CommonLisp" "Elixir"] state :language]

           [:h2 "popover"]
           [:p/popover {:color "green"
                        :placement "right"
                        :button-text "trees-r"}
            [:p/tooltip {:color "green"
                         :title  "tree"
                         :content "How many trees are in a forest?"}]]

           [:h2 "tabs"]
           [:p/tabs
            [:p/tab {:title "a"
                     :isActive false
                     :color "red"
                     :tabIndex 1}
             [:h4 "We love the A-team !"]]
            [:p/tab {:title "b"
                     :isActive true
                     :color "green"
                     :tabIndex 0}
             [:h4 "Bananas are a great potassium source!"]]]]
   :fns {:incr (fn [_ s] (inc s))}}
  {:fns {}}))
