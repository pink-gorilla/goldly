(ns goldly.puppet.db
  (:require
   [re-frame.core :refer [reg-event-db]]
   [taoensso.timbre :as timbre :refer-macros [trace tracef
                                              debug debugf
                                              info infof
                                              warnf
                                              error errorf]]))

(def initial-db
  {; :route {:route-params {} :handler :ui/main}
          ;{:route-params {:item-id "1"} :handler :a-item}
   :systems []
   :id nil

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

#_(reg-event-db
 :goldly/nav
 (fn [db [_ route]]
   (infof "nav: %s " route)
   (assoc db :route route)))

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
