(ns goldly.code.core
  (:require
   [pinkie.pinkie :refer-macros [register-component]]
   [goldly.code.viewer :refer [code-viewer]]))

(register-component :p/code code-viewer)

