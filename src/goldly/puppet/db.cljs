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

(defn find-system-by-id [db system-id]
  (let [systems (vals (get-in [:goldly :running-systems] db))]
    (->
     (filter (fn [s] (= system-id (:id s))) systems)
     (first))))

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

(reg-event-db
 :goldly/add-running-system
 (fn [db [_ id system]]
   (info "adding running goldly system: " id)
   (assoc-in db [:goldly :running-systems id] system)))

(reg-event-db
 :goldly/remove-running-system
 (fn [db [_ id]]
   (info "removing running goldly system: " id)
   (update-in db [:goldly :running-systems] dissoc id)))
