(ns goldly.events
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [goldly.extension.pinkie :refer [add-extension-pinkie-static]]
   [goldly.notebook-loader.clj-list :refer [start-watch-notebooks]]
   [goldly.notebook-loader.demo :refer [load-demo-notebooks]]))

(def initial-db
  {:id nil
   ; system ui
   :running-systems {}})

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (let [db (or db {})]
     (info "goldly starting ..")
     (add-extension-pinkie-static)
     (load-demo-notebooks)
     (start-watch-notebooks)
     (rf/dispatch [:nrepl/init])
     (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])
     (assoc-in db [:goldly] initial-db))))

(rf/reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/clj-result data])
     db)))

