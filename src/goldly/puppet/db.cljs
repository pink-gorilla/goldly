(ns goldly.puppet.db
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch-sync dispatch]]
   [taoensso.timbre :as timbre :refer-macros [trace tracef
                                              debug debugf
                                              info infof
                                              warnf
                                              error errorf]]
   #_[pinkgorilla.events.helper :refer [standard-interceptors]]))

(def initial-db
  {; system explorer
   :main :info
   :systems []
   :id nil
   :system nil
   ; system ui
   :running-systems {}})

(defn find-system-by-id [db system-id]
  (let [systems (vals (:running-systems db))]
    (->
     (filter (fn [s] (= system-id (:id s))) systems)
     (first))))

(reg-event-db
 :db-init
 (fn [_ _]
   (info "initializing app-db ..")
   initial-db))

(reg-event-db
 :goldly/nav
 (fn [db [_ data id]]
   (infof "nav: %s %s" data id)
   (assoc db
          :main data
          :id id)))

(reg-event-db
 :goldly/systems-store
 (fn [db [_ data]]
   (info "available goldly systems: " data)
   (assoc db :systems data)))

(reg-event-db
 :goldly/system-store
 (fn [db [_ system]]
   (info "running goldly system: " system)
   (assoc db :system system)))

(reg-event-db
 :goldly/add-running-system
 (fn [db [_ id system]]
   (info "adding running goldly system: " id)
   (assoc-in db [:running-systems id] system)))

(reg-event-db
 :goldly/remove-running-system
 (fn [db [_ id]]
   (info "removing running goldly system: " id)
   (update-in db [:running-systems] dissoc id)))
