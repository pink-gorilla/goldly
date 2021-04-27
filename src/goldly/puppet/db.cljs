(ns goldly.puppet.db
  (:require
   [re-frame.core :refer [reg-event-db]]
   [taoensso.timbre :as timbre :refer-macros [info]]))

(def initial-db
  {:systems []
   :id nil
   ; system ui
   :running-systems {}})

(reg-event-db
 :db-init
 (fn [db _]
   (let [db (or db {})]
     (info "initializing goldly app-db ..")
     (assoc-in db [:goldly] initial-db))))

(reg-event-db
 :goldly/systems-store
 (fn [db [_ data]]
   (info "available goldly systems: " data)
   (assoc-in db [:goldly :systems] data)))

(reg-event-db
 :goldly/system-store
 (fn [db [_ system]]
   (info "running goldly system: " system)
   (assoc-in db [:goldly :system] system)))

