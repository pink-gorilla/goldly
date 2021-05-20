(ns goldly.runner.db
  (:require
   [re-frame.core :refer [reg-event-db]]
   [taoensso.timbre :as timbre :refer-macros [info]]))

(defn find-system-by-id [db system-id]
  (let [systems (vals (get-in db [:goldly :running]))
        matching (filter (fn [s] (= system-id (:id s))) systems)
        m (first matching)]
    m))

(reg-event-db
 :goldly/add-running-system
 (fn [db [_ id system]]
   (info "adding running goldly system: " id)
   (let [system-run-id (assoc system :run-id id)]
     (assoc-in db [:goldly :running id] system-run-id))))

(reg-event-db
 :goldly/remove-running-system
 (fn [db [_ id]]
   (info "removing running goldly system: " id)
   (update-in db [:goldly :running] dissoc id)))
