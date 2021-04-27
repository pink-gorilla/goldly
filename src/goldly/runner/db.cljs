(ns goldly.runner.db
  (:require
   [re-frame.core :refer [reg-event-db]]
   [taoensso.timbre :as timbre :refer-macros [info]]))

(defn find-system-by-id [db system-id]
  (let [systems (vals (get-in [:goldly :running-systems] db))]
    (->
     (filter (fn [s] (= system-id (:id s))) systems)
     (first))))

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
