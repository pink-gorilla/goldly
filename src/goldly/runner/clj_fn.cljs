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
    (let [_ (info "specter where: " where)
          where-resolved (specter-resolve where)
          _ (info "specter resolved: " where-resolved)]
      (reset! state (setval where-resolved result @state))
      (debug "update state from clj success!")
      (debug "system state after clj-update: " (pr-str state)))
    (catch :default e
      (error "exception in updating state after clj result call:" e))))

(reg-event-db
 :goldly/clj-result
 (fn [db [_ {:keys [run-id system-id fun result error where] :as data}]]
   (let [system (if (nil? run-id)
                  (find-system-by-id db system-id)
                  (get-in db [:goldly :running-systems run-id]))
         update-state (:update-state system)]
     (debug "rcvd clj result: " data)
     (if system
       (if (and result where update-state)
         (if update-state
           (update-state result where)
           (error "clj-result update-state missing. data: " data))
         (error "clj-result failed requirement: {:result :where :update-state}"))
       (error "received clj result for unknown system-id " system-id))
     db)))
