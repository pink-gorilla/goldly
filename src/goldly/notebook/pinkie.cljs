(ns goldly.notebook.pinkie
  (:require
   [pinkie.pinkie :refer-macros [register-component]]
   [goldly.system :refer [system]]))

(register-component :p/goldly system)
