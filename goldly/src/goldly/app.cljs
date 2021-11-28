(ns goldly.app
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [webly.build.prefs :refer [pref]]
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

(defn goldly-start [static?]
  (info "goldly starting ..")
  (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])
  (add-extension-pinkie-static)
  (go (<! (loader/load-cljs static?)) ; await for cljs auto-load to be finised before showing ui.
      (rf/dispatch [:webly/status :running])))

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (info "goldly.init..")
   (let [db (or db {})
         pref (pref)
         profile (:profile pref)
         static? (= "static" profile)]
     (error "goldly.PREF:. " pref)
     (when static?
       (error "goldly.static.mode: " static?)
       (goldly-start static?))
     (-> db
         (assoc-in [:goldly] initial-db)
         (assoc-in [:pref] pref)))))

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
   (goldly-start false)
   ;(goldly-start true) ; testing as a static

   nil))