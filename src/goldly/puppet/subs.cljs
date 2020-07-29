(ns goldly.puppet.subs
  (:require-macros
   [reagent.ratom])
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :systems
 (fn [db _]
   (:systems db)))

(reg-sub
 :system
 (fn [db _]
   (:system db)))

