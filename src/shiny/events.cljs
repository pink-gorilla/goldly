(ns shiny.events
  "process-instructions from shiny clj server"
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch-sync dispatch]]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf info infof warnf errorf debug)]
   #_[pinkgorilla.events.helper :refer [standard-interceptors]]))

(def initial-db
  {:main :notebook
   :systems [1 2 3]})

(reg-event-db
 :db-init
 (fn [_ _]
   (info "initializing app-db ..")
   initial-db))

(reg-event-fx
 :ws-open
 (fn [cofx [_ ?data]]
   (debugf "Channel socket successfully established!: %s" ?data)))


(reg-event-db
 :notebook-add-code
; [standard-interceptors]
 (fn [db [_ {:keys [id code]}]]
   (let [_ (debugf "code-add id: %s code: %s" id code)]
     db ; (assoc-in db [:worksheet] worksheet-new)
     )))
