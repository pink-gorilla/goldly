(ns systems.greeter-details
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(system-start!
 (goldly/system
  {:id :greeter-details
   :hidden true
   :state {}
   :html  [:div
           [:p "this shows how to do master-detail relations"]
           [:p "Access this component only via greeter."]
           [:p.bg-blue-300.mg-3 "the best dad in the world is: " ext]]
   :fns {}}
  {:fns {}}))
