(ns goldly.require-clj
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info]]))

(defn require-clj-namespaces [namespaces]
  (doall (for [s namespaces]
           (do (info "loading ns: " s)
               (require s))))
  namespaces)

(comment
  (require 'systems.components 'clojure.string)
;
  )