(ns goldly-server.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :goldly-server/init
 (fn [db [_]]
   (info "goldly starting ..")
   (rf/dispatch [:goldly/init])
   (rf/dispatch [:webly/status :running])
   db))

