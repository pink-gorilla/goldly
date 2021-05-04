(ns goldly.puppet.subs
  (:require-macros
   [reagent.ratom])
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :systems
 (fn [db _]
   (get-in db [:goldly :systems])))

(reg-sub
 :goldly/system
 (fn [db [_ id]]
   (get-in db [:goldly :system id])))

