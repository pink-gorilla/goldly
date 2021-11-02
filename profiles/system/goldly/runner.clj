(ns goldly.runner
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [info]]
   [goldly.component.type.system :refer [add-system]]))

(defrecord GoldlySystem [id])

(defn system-start!
  [system]
  (let [id (:id system)]
    (info "starting system " id)
    (add-system system)
    (GoldlySystem. id)))





