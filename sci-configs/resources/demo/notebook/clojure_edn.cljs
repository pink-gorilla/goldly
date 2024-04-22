(ns demo.notebook.clojure-edn
  (:require
   [clojure.edn :as edn]))

(def x (pr-str {:a 1 :b true :name "harry potter"}))

(edn/read-string x)
