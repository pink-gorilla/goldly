(ns demo.notebook
  (:require
   [reval.document.collection :as r]
   [modular.config :refer [get-in-config]]))

(r/nb-collections)

(get-in-config [:reval :collections])

(get-nss-list :clj ["demo/notebook"])
