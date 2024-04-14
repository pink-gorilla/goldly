(ns goldly.run.app
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof warn error]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [webly.build.prefs :refer-macros [pref]]
   [webly.build.lazy :as webly-lazy]
   [goldly.offline.old :refer [static?]]
   [goldly.sci.kernel-cljs]
   [goldly.run.lazy-ext-css :as lazy-ext-css]
   [goldly.cljs.loader :refer [load-cljs]]
   [goldly.service.core]
   ; [goldly.offline.app] ; testing only!!!
   [shadow.loader :as l]
   [goldly.run.page :refer [get-page-fn]]
   [frontend.page :refer [set-resolver!]]))

(defn goldly-start [static?]
  (info "goldly starting .. static?: " static?)
   ;(goldly.offline.app/patch-path) ; testing only
  (l/init "") ; prefix to the path loader
  (reset! webly-lazy/on-load lazy-ext-css/goldly-on-load)
  (set-resolver! get-page-fn)
  (rf/dispatch [:ga/event {:category "goldly" :action "started" :label 77 :value 13}])
  (go (<! (load-cljs static?)) ; await for cljs auto-load to be finised before showing ui.
      (rf/dispatch [:webly/status :running])))

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (warn "goldly.init..")
   (let [db (or db {})
         static? (static?)]
     (when static?
       (error "goldly.static.mode: " static?)
       (goldly-start static?))
     (-> db
         (assoc-in [:pref] (pref))))))

(rf/reg-event-fx
 :ws/open-first
 (fn [cofx [_ new-state-map]]
   (infof "websocket successfully established!: %s" new-state-map)
   (goldly-start false)
   ;(goldly-start true) ; testing as a static
   nil))
