(ns goldly.app
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [goldly.cljs.loader :as loader]
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   
   [goldly.service.core]
   [goldly.sci.kernel-cljs]
   [goldly.extension.lazy]
   [goldly.extension.pinkie :refer [add-extension-pinkie-static]]))

(def initial-db
  {:id nil
   ; system ui
   :running-systems {}})

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (let [db (or db {})]
     (assoc-in db [:goldly] initial-db))))

(rf/reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/clj-result data])
     db)))

(rf/reg-event-fx
 :ws/open-first
 (fn [cofx [_ new-state-map]]
   (infof "websocket successfully established!: %s" new-state-map)
   (info "goldly starting ..")
   (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])

   ;(request-systems)
   (add-extension-pinkie-static)

   (go (<! (loader/load-cljs)) ; await for cljs auto-load to be finised before showing ui.
       (rf/dispatch [:webly/status :running]))

   ;(start-watch-notebooks)
   ;(rf/dispatch [:nrepl/init])

   #_(go (<! (timeout 1000))
         (rf/dispatch [:webly/status :running]))

   nil))