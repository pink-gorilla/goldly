(ns goldly.app-system
  (:require
   [goldly.app]
   [goldly.component.type.notebook :refer [notebook-watch]]
   [goldly.component.type.system]
   [goldly.notebook.picasso]
   [goldly.scratchpad.handler]
   [goldly.scratchpad.core]
   [goldly.broadcast.core]
   [pinkgorilla.nrepl.service]))


(defn golly-run []
  (notebook-watch)

  (pinkgorilla.nrepl.service/start-nrepl (get-in-config [:nrepl])))
