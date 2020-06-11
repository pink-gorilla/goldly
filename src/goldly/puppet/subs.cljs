(ns goldly.puppet.subs
  (:require-macros
   [reagent.ratom])
  (:require
   [taoensso.timbre :refer-macros (info)]
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :main
 (fn [db _]
   (:main db)))

(reg-sub
 :systems
 (fn [db _]
   (:systems db)))

(reg-sub
 :system-id
 (fn [db _]
   (:id db)))

(reg-sub
 :system
 (fn [db _]
   (:system db)))

