(ns systems.components
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

(println "loading systems.components ..")

(system-start!
 (goldly/system
  {:name "components"
   :state 42
   :html  [:p/componentsui]
   :fns {}}
  {:fns {}}))