(ns goldly.events
  "process-instructions from goldly clj server"
  (:require

   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [goldly.extension.pinkie :refer [add-extension-pinkie-static]]))

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
     (add-extension-pinkie-static)
     (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])
     (assoc-in db [:goldly] initial-db))))

(rf/reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/clj-result data])
     db)))

