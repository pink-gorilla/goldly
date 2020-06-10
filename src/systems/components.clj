(ns systems.components
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(println "loading systems.components ..")

(system-start!
 (goldly/system
  {:name "components"
   :state 42
   :html  [:p/componentsui]
   :fns {}}
  {:fns {}}))