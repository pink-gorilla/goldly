(ns goldly-server.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db
 :goldly-server/init
 (fn [db [_]]
   (info "goldly starting ..")
   (dispatch [:ga/event {:category "goldly-server" :action "started" :label 77 :value 13}])
   (dispatch [:webly/status :running])
   db))

