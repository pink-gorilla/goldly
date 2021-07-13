(ns goldly.system.ui
  (:require
   [goldly.component.ui :refer [component]]))
(defn system-ext
  "requests system with id from server and displays it."
  [id ext]
  [component :system id {:ext ext}])

(defn system [id]
  [system-ext id ""])
