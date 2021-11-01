(ns goldly.system.ws
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [webly.ws.core :refer [send!]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.system.db :refer [find-system-by-id]]))

(defmethod -event-msg-handler :goldly/systems
  [{:keys [?data] :as ev-msg}]
  (debugf "systems received: %s" ev-msg)) ; ?data)))

(rf/reg-event-fx
 :goldly/send ; send data to clj. used by get-system + clj fn dispatch
 (fn [cofx [_ & data]] ; strip off :goldly/send from args vector
   ; example for data: 
   ; [:goldly/send :goldly/dispatch id fun-name arg1 arg2]
   ; [:goldly/send :goldly/system id]
   (let [data-v (into [] data)
         goldly-tag (first data-v)]
     (infof "goldly/send %s " data-v)
     (try
       (send! data-v) ; 5000 (fn [data] ; [event-type data]]
                      ;           (info "send data:" data)
                      ;           (dispatch [:goldly/event goldly-tag data])))
       (catch :default e
         (error "exception sending to clj: " e)))
     (info "goldly/send done.")
     nil)))

(defn request-systems []
  (try
    (infof "goldly/send %s "  [:goldly/systems])
    (send! [:goldly/systems]
           ;     5000
           ;     (fn [[event-type data]]
           ;       (info "systems data:" data)
           ;       (dispatch [:goldly/systems-store data]))
           )
    (catch js/Error e (error "send event to server ex: " e))))

(rf/reg-event-fx
 :goldly/get-running-system
 (fn [cofx [_ system-id]]
   (let [s (find-system-by-id (:db cofx) system-id)]
     (infof "running system data: %s" s)
     nil)))

