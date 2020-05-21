(ns shiny.subs
  (:require-macros
   [reagent.ratom])
  (:require
   [taoensso.timbre :refer-macros (info)]
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :systems
 (fn [db _]
   (:systems db)))

(reg-sub
 :main
 (fn [db _]
   (:main db)))

(reg-sub
 :system-id
 (fn [db _]
   (:id db)))