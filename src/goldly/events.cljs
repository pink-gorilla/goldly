(ns goldly.events
  "process-instructions from goldly clj server"
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]))

(def initial-db
  {:systems []
   :id nil
   ; system ui
   :running-systems {}})

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (let [db (or db {})]
     (info "goldly starting ..")

     (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])
     (assoc-in db [:goldly] initial-db))))

(rf/reg-event-db
 :goldly/systems
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/systems-store data])
     db)))

(rf/reg-event-db
 :goldly/system
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/system-store data])
     db)))

(rf/reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/clj-result data])
     db)))

