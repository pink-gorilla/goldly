(ns goldly.puppet.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :goldly/systems
 (fn [db _]
   (get-in db [:goldly :systems])))

(reg-sub
 :goldly/system
 (fn [db [_ id]]
   (get-in db [:goldly :system id])))

