(ns systems.help
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(system-start!
 (goldly/system
  {:name "help"
   :route "/help"
   :state 42
   :html  [:div
           [:h1 "Goldly"]
           [:p "no docs written yet..."]]
   :fns {}}
  {:fns {}}))

