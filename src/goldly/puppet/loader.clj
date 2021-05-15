(ns goldly.puppet.loader
  "loads systems from namespaces"
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info]]))

(defn load-components-namespaces [namespace-symbols]
  (doall (for [s namespace-symbols]
           (do (info "loading system in ns: " s)
               (require s))))
  namespace-symbols)

(comment
  (require 'systems.components 'clojure.string)
;
  )