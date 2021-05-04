(ns goldly.puppet.db
  (:require
   [re-frame.core :refer [reg-event-db]]
   [taoensso.timbre :as timbre :refer-macros [info]]))

(reg-event-db
 :goldly/systems-store
 (fn [db [_ data]]
   (info "available goldly systems: " data)
   (assoc-in db [:goldly :systems] data)))

(reg-event-db
 :goldly/system-store
 (fn [db [_ system]]
   (info "rcvd system definition: " system)
   (assoc-in db [:goldly :system (:id system)] system)))

