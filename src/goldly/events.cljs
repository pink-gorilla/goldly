(ns goldly.events
  "process-instructions from goldly clj server"
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch-sync dispatch]]
   [taoensso.timbre :as timbre :refer-macros (tracef trace debugf info infof warnf errorf error debug)]
   [goldly.ws :refer [chsk-send! send! start-router! -event-msg-handler]]
   #_[pinkgorilla.events.helper :refer [standard-interceptors]]))

(def initial-db
  {:main :info
   :systems []
   :id nil
   :system nil})

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
 :goldly/send
 (fn [cofx [_ event-name data]]
   (infof "goldly/send %s %s" event-name data)
   (chsk-send! [event-name data])
   nil))

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
       :goldly/system (dispatch [:goldly/system-store data])
       :goldly/systems (dispatch [:goldly/systems-store data])
       (infof "goldly Unhandled server event %s %s" event-type data))
     db)))

(defmethod -event-msg-handler :goldly/systems
  [{:keys [?data] :as ev-msg}]
  ;(let [[?uid ?csrf-token ?handshake-data] ?data]
  (debugf "systems received: %s" ev-msg)) ; ?data)))