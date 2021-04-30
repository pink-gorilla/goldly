(ns goldly.notebook.pinkie
  (:require
   [pinkie.pinkie :refer-macros [register-component]]
   [goldly.views.system :refer [system]]))

(register-component :p/goldly system)
