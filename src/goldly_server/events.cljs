(ns goldly-server.events
  (:require
   [taoensso.timbre :as timbre :refer-macros [info infof]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [re-frame.core :as rf]
   [goldly.cljs.loader :as loader]))

(rf/reg-event-db
 :goldly-server/init
 (fn [db [_]]
   (info "goldly starting ..")
   (rf/dispatch [:goldly/init])

   ; set running on websocket connect.
   ;(rf/dispatch [:webly/status :running])
   db))

(rf/reg-event-fx
 :ws/open-first
 (fn [cofx [_ new-state-map]]
   (infof "websocket successfully established!: %s" new-state-map)
   ;(request-systems)
   (loader/load-cljs)
   (rf/dispatch [:webly/status :running])
   #_(go (<! (timeout 1000))
         (rf/dispatch [:webly/status :running]))

   nil))
