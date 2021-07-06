(ns goldly.scratchpad.core
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [goldly.system.db :refer [find-system-by-id]]
   [goldly.system.sci :refer [run-state]]
   [pinkgorilla.repl.clipboard :refer [clipboard-set]]))

(rf/reg-event-fx
 :goldly/scratchpad-get
 (fn [cofx [_]]
   (if-let [s (find-system-by-id (:db cofx) :scratchpad)]
     (do
       (infof "running system data: %s" s)
       (let [run-id (get-in s [:run-id])
             state (get @run-state run-id)
             snippet (:snippet @state)
             snippet (select-keys snippet [:type :src])]
         (info "scratchpad get:" snippet)
         (rf/dispatch [:goldly/send :goldly/scratchpad snippet])))
     (do (error "scratchpad not running")
         (rf/dispatch [:goldly/send :goldly/scratchpad {:type :nil :src ""}])))
   nil))

(rf/reg-event-fx
 :goldly/scratchpad-set
 (fn [cofx [_ {:keys [type src]}]]
   (if-let [s (find-system-by-id (:db cofx) :scratchpad)]
     (do
       (infof "running system data: %s" s)
       (let [run-id (get-in s [:run-id])
             state (get @run-state run-id)]
         (info "scratchpad-set: type: " type " src:" src)
         ;(swap! state assoc-in [:snippet :src] src)
         (clipboard-set {:type type
                         :src src
                         :id :scratchpad-system})
         (swap! state assoc-in [:snippet :new-system] true)
         ;(nav :goldly/system :system-id :scratchpad)
         ))

     (do (error "scratchpad not running")
         #_(rf/dispatch [:goldly/send :goldly/scratchpad {:type :nil :src ""}])))
   nil))
