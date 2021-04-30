(ns goldly.runner
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [info]]
   [goldly.puppet.db :refer [add-system]]
   ;[goldly.system :refer [system->cljs]]
   ))

(defrecord GoldlySystem [id])

(defn system-start!
  [system]
  (let [id (:id system)]
    (info "starting system " id)
    (add-system system)
  ;(system->cljs system)
    (GoldlySystem. id)))





