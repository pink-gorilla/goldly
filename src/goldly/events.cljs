(ns goldly.events
  "process-instructions from goldly clj server"
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch-sync dispatch]]
   [taoensso.timbre :as timbre :refer-macros (tracef trace debugf info infof warnf errorf error debug)]
   [goldly.ws :refer [chsk-send! send! start-router! -event-msg-handler]]
   #_[pinkgorilla.events.helper :refer [standard-interceptors]]))

(def initial-db
  {; system explorer
   :main :info
   :systems []
   :id nil
   :system nil
   ; system ui
   :running-systems {}})

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

(defn request-systems []
  (try
    (chsk-send! [:goldly/systems]
                5000
                (fn [[event-type data]]
                  (info "systems data:" data)
                  (dispatch [:goldly/systems-store data])))
    (catch js/Error e (error "send event to server ex: " e))))

(reg-event-fx
 :goldly/send ; send data to clj. used by get-system + clj fn dispatch
 (fn [cofx [_ & data]] ; strip off :goldly/send from args vector
   ; example for data: 
   ; [:goldly/send :goldly/dispatch id fun-name arg1 arg2]
   ; [:goldly/send :goldly/system id]
   (let [data-v (into [] data)]
     (infof "goldly/send %s " data-v)
     (try
       (chsk-send! data-v)
       (catch :default e
         (error "exception sending to clj: " e)))
     (info "goldly/send done.")
     nil)))

(reg-event-fx
 :goldly/ws-open
 (fn [cofx [_ new-state-map]]
   (debugf "websocket successfully established!: %s" new-state-map)
   (request-systems)
   nil))

(reg-event-db
 :goldly/event
 (fn [db [_ event-type data]]
   (let [_ (debugf "rcvd :goldly/event: %s %s" event-type data)]
     (case event-type
       :chsk/ws-ping (trace "goldly ping rcvd")
       :goldly/systems (dispatch [:goldly/systems-store data])
       :goldly/system (dispatch [:goldly/system-store data])
       :goldly/dispatch (dispatch [:goldly/clj-result data])
       (infof "goldly Unhandled server event %s %s" event-type data))
     db)))

(defmethod -event-msg-handler :goldly/systems
  [{:keys [?data] :as ev-msg}]
  ;(let [[?uid ?csrf-token ?handshake-data] ?data]
  (debugf "systems received: %s" ev-msg)) ; ?data)))


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

(reg-event-db
 :goldly/clj-result
 (fn [db [_ {:keys [run-id system-id fun result error where] :as data}]]
   (let [system (get-in db [:running-systems run-id])
         update-state (:update-state system)]
     (info "rcvd clj result: " data " for system: " system)
     (when (and result where update-state)
       (update-state result where))
     db)))
