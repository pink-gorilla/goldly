(ns goldly.events
  "process-instructions from goldly clj server"
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [goldly.web.ws :refer [chsk-send! send! start-router! -event-msg-handler]]
   [goldly.puppet.db :refer [find-system-by-id]]))

(reg-event-fx
 :goldly/send ; send data to clj. used by get-system + clj fn dispatch
 (fn [cofx [_ & data]] ; strip off :goldly/send from args vector
   ; example for data: 
   ; [:goldly/send :goldly/dispatch id fun-name arg1 arg2]
   ; [:goldly/send :goldly/system id]
   (let [data-v (into [] data)
         goldly-tag (first data-v)]
     (infof "goldly/send %s " data-v)
     (try
       (chsk-send! data-v 5000 (fn [data] ; [event-type data]]
                                 (info "send data:" data)
                                 (dispatch [:goldly/event goldly-tag data])))
       (catch :default e
         (error "exception sending to clj: " e)))
     (info "goldly/send done.")
     nil)))

(defn request-systems []
  (try
    (infof "goldly/send %s "  [:goldly/systems])
    (chsk-send! [:goldly/systems]
                5000
                (fn [[event-type data]]
                  (info "systems data:" data)
                  (dispatch [:goldly/systems-store data])))
    (catch js/Error e (error "send event to server ex: " e))))

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
  (debugf "systems received: %s" ev-msg)) ; ?data)))


(reg-event-db
 :goldly/clj-result
 (fn [db [_ {:keys [run-id system-id fun result error where] :as data}]]
   (let [_ (debug "running systems: " (get-in db [:running-systems]))
         system (if (nil? run-id)
                  (find-system-by-id db system-id)
                  (get-in db [:running-systems run-id]))
         update-state (:update-state system)]
     (debug "rcvd clj result: " data) ; " for system: " system
     (if system
       (if (and result where update-state)
         (update-state result where)
         (error "clj result update requirements not met."))
       (error "received clj result for unknown system-id " system-id))
     db)))
