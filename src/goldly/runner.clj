(ns goldly.runner
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [goldly.puppet.db :refer [get-system add-system]]
   [goldly.system :refer [system->cljs]]))

;; system


(defn system-start!
  [system]
  (info "starting system " (:id system))
  (add-system system)
  (system->cljs system))





