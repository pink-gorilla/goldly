(ns goldly.broadcast.core
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [goldly.system.db :refer [find-system-by-id]]))

(defn clj-response-valid? [{:keys [system-id where]}]
  (and (vector? where)
       (keyword? system-id)))

(defn get-running-system [db {:keys [run-id system-id result error where] :as data}]
  (if (nil? run-id)
    (find-system-by-id db system-id)
    (get-in db [:goldly :running run-id])))

(defn safe [update-state result where]
  (try
    (update-state result where)
    (catch :default e
      (error "exception in updating state:" e))))

(rf/reg-event-db
 :goldly/clj-result
 (fn [db [_ {:keys [run-id system-id result error where] :as data}]]
   (info ":goldly/clj-result run-id:" run-id)
   (if (clj-response-valid? data)
     (if-let [system (get-running-system db data)]
       (if-let [update-state (get-in system [:update-state])]
         (safe update-state result where)
         (taoensso.timbre/error "clj-result update-state missing. data: " data))
       (taoensso.timbre/debug "received clj result for unknown system-id " system-id))
     (taoensso.timbre/error "clj-result failed requirement: {:system-id :where}"))
   db))

(rf/reg-event-db
 :goldly/set-system-state
 (fn [db [_ {:keys [system-id result where] :as data}]]
   (if (clj-response-valid? data)
     (if-let [system (get-running-system db data)]
       (if-let [update-state (get-in system [:update-state])]
         (safe update-state result where)
         (taoensso.timbre/error "set-system-state update-state missing. data: " data))
       (taoensso.timbre/debug "set-system-state result for unknown system-id " system-id))
     (taoensso.timbre/error "set-system-state failed requirement: {:system-id :where}"))
   db))