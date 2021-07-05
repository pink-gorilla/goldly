(ns goldly.system.require
  "loads systems from namespaces"
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info]]))

(defn require-namespaces [namespaces]
  (doall (for [s namespaces]
           (do (info "loading ns: " s)
               (require s))))
  namespaces)

(comment
  (require 'systems.components 'clojure.string)
;
  )