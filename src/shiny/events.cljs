(ns shiny.events
  "process-instructions from shiny clj server"
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch-sync dispatch]]
   [taoensso.timbre :as timbre :refer-macros (tracef trace debugf info infof warnf errorf error debug)]
   [shiny.ws :refer [chsk-send! send! start-router! -event-msg-handler]]
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
 :shiny/nav
 (fn [db [_ data id]]
   (infof "nav: %s %s" data id)
   (assoc db
          :main data
          :id id)))

(reg-event-db
 :shiny/systems-store
 (fn [db [_ data]]
   (info "rcvd shiny systems: " data)
   (assoc db :systems data)))

(reg-event-db
 :shiny/system-store
 (fn [db [_ system]]
   (assoc db :system system)))

(defn request-systems []
  (try
    (chsk-send! [:shiny/systems]
                5000
                (fn [[event-type data]]
                  (info "systems data:" data)
                  (dispatch [:shiny/systems-store data])))
    (catch js/Error e (error "send event to server ex: " e))))

(reg-event-fx
 :shiny/send
 (fn [cofx [_ event-name data]]
   (infof "shiny/send %s %s" event-name data)
   (chsk-send! [event-name data])
   nil))

(reg-event-fx
 :shiny/ws-open
 (fn [cofx [_ new-state-map]]
   (debugf "websocket successfully established!: %s" new-state-map)
   (request-systems)
   nil))

(reg-event-db
 :shiny/event
 (fn [db [_ event-type data]]
   (let [_ (debugf "rcvd :shiny/event: %s %s" event-type data)]
     (case event-type
       :chsk/ws-ping (trace "shiny ping rcvd")
       :shiny/system (dispatch [:shiny/system-store data])
       :shiny/systems (dispatch [:shiny/systems-store data])
       (infof "shiny Unhandled server event %s %s" event-type data))
     db)))

(defmethod -event-msg-handler :shiny/systems
  [{:keys [?data] :as ev-msg}]
  ;(let [[?uid ?csrf-token ?handshake-data] ?data]
  (debugf "systems received: %s" ev-msg)) ; ?data)))