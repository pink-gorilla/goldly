(ns goldly-server.app
  (:require
   [taoensso.timbre :as timbre :refer-macros [info infof]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [re-frame.core :as rf]
   [goldly.cljs.loader :as loader]
   [goldly.app]
   [goldly.extension.pinkie :refer [add-extension-pinkie-static]]
   [goldly.notebook-loader.clj-list :refer [start-watch-notebooks]]
   [goldly-server.developer-tools]))

(rf/reg-event-db
 :goldly-server/init
 (fn [db [_]]
   (info "goldly starting ..")
   (rf/dispatch [:goldly/init])
   db))

(rf/reg-event-fx
 :ws/open-first
 (fn [cofx [_ new-state-map]]
   (infof "websocket successfully established!: %s" new-state-map)
   (info "goldly starting ..")
   (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])

   ;(request-systems)
   (add-extension-pinkie-static)

   (go (<! (loader/load-cljs)) ; await for cljs auto-load t be finised before showing ui.
       (rf/dispatch [:webly/status :running]))

   (start-watch-notebooks)
   (rf/dispatch [:nrepl/init])

   #_(go (<! (timeout 1000))
         (rf/dispatch [:webly/status :running]))

   nil))