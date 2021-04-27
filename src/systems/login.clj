(ns systems.help
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(system-start!
 (goldly/system
  {:id :login
   :state 42
   :html  [:div
           [:p "This demo shows what happens if you use components that are not available."]
           [:p/login]]
   :fns {}}
  {:fns {}}))

