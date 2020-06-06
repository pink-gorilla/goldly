(ns goldly.systems.components
  (:require
   [goldly.system :as goldly :refer [def-ui]]))

(def components
  (goldly/system
   {:name "components"
    :state 42
    :html  [:p/components]
    :fns {}}
   {:fns {}}))

