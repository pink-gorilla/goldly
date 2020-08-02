(ns systems.components
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(system-start!
 (goldly/system
  {:name "components"
   :route "/"
   :state 42
   :html  [:p/componentsui]
   :fns {}}
  {:fns {}}))