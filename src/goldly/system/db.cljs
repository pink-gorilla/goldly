(ns goldly.system.db
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [debugf info]]))

(defn find-system-by-id [db system-id]
  (let [systems (vals (get-in db [:goldly :running]))
        matching (filter (fn [s] (= system-id (:id s))) systems)
        m (first matching)]
    m))

(rf/reg-event-db
 :goldly/add-running-system
 (fn [db [_ id system]]
   (info "adding running goldly system: " id)
   (let [system-run-id (assoc system :run-id id)]
     (assoc-in db [:goldly :running id] system-run-id))))

(rf/reg-event-db
 :goldly/remove-running-system
 (fn [db [_ id]]
   (info "removing running goldly system: " id)
   (update-in db [:goldly :running] dissoc id)))

(rf/reg-event-db
 :goldly/systems-store
 (fn [db [_ data]]
   (info "available goldly systems: " data)
   (assoc-in db [:goldly :systems] data)))

(rf/reg-event-db
 :goldly/system-store
 (fn [db [_ system]]
   (info "rcvd system definition: " (:id system))
   (assoc-in db [:goldly :system (:id system)] system)))

(rf/reg-event-db
 :goldly/systems
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/systems-store data])
     db)))

(rf/reg-event-db
 :goldly/system
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/system-store data])
     db)))