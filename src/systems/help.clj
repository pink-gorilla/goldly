(ns systems.help
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

(println "loading systems.help ..")

(system-start!
 (goldly/system
  {:name "help"
   :state 42
   :html  [:div
           [:h1 "Goldly"]]
   :fns {}}
  {:fns {}}))

