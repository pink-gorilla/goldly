(ns demo.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db
 :demo/init
 (fn [db [_]]
   (info "goldly starting ..")
   (dispatch [:ga/event {:category "goldly-demo" :action "started" :label 77 :value 13}])
   (dispatch [:webly/status :running])
   db))

