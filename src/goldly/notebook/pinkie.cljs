(ns goldly.notebook.pinkie
  (:require
   [pinkie.pinkie :refer-macros [register-component]]
   [goldly.system.ui :refer [system]]))

(register-component :p/goldly system)
