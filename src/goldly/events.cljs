(ns goldly.events
  "process-instructions from goldly clj server"
  (:require
   [re-frame.core :refer [reg-event-db  dispatch]]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]))

(def initial-db
  {:systems []
   :id nil
   ; system ui
   :running-systems {}})

(reg-event-db
 :goldly/init
 (fn [db _]
   (let [db (or db {})]
     (info "goldly starting ..")
     (assoc-in db [:goldly] initial-db))))

(reg-event-db
 :goldly/systems
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (dispatch [:goldly/systems-store data])
     db)))

(reg-event-db
 :goldly/system
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (dispatch [:goldly/system-store data])
     db)))

(reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (dispatch [:goldly/clj-result data])
     db)))

