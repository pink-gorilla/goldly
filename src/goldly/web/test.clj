(ns goldly.web.test
  (:require
   [clojure.string]
   [clojure.java.io :as io]))

(defn check-resource  [name]
  (let [;resource (io/file name)
        resource (io/resource name)]
    (if resource
      true
      false)))

(if (check-resource "images/marker-icon.png")
  (println "Great - gorilla-ui resources are working!")
  (println "FUCK - gorila-ui resources not working!"))