(ns goldly.runner.clj-fn
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [clojure.walk :as walk]
   [com.rpl.specter :refer [transform setval END]]
   [goldly.runner.db :refer [find-system-by-id]]))

(defn clj-fun [run-id system-id fn-clj]
  (infof "wrapping clj-fn run-id:%s system-id:%s fn:%s" run-id system-id fn-clj)
  (fn [& args]
    (let [fn-vec [run-id system-id fn-clj]
          fn-vec (if args (into fn-vec args) fn-vec)]
      (infof "runner %s : system %s calling fn-clj %s args:" run-id system-id fn-clj fn-vec)
      (dispatch [:goldly/send :goldly/dispatch fn-vec])
      nil)
    nil))

(defn specter-resolve
  [specter-vector]
  (walk/prewalk
   (fn [x] (if (keyword? x)
             (case x
               :END END
               x)
             x))
   specter-vector))

(defn update-state-from-clj-result [state result where]
  (debugf "updating state from clj result: %s where: %s" result where)
  (try
   ;(com.rpl.specter/setval [:a] 1 m) set key a to 1 in m
    (let [_ (debug "specter where: " where)
          where-resolved (specter-resolve where)
          _ (debug "specter resolved: " where-resolved)]
      (reset! state (setval where-resolved result @state))
      (debug "update state from clj success!")
      (debug "system state after clj-update: " (pr-str state)))
    (catch :default e
      (error "exception in updating state after clj result call:" e))))

(defn clj-response-valid? [{:keys [system-id where]}]
  (and (vector? where)
       (keyword? system-id)))

(defn get-running-system [db {:keys [run-id system-id result error where] :as data}]
  (if (nil? run-id)
    (find-system-by-id db system-id)
    (get-in db [:goldly :running-systems run-id])))

(defn safe [update-state result where]
  (try
    (update-state result where)
    (catch :default e
      (error "exception in updating state:" e))))

(reg-event-db
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

(reg-event-db
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