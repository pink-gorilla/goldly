(ns goldly.notifications.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :notifications
 (fn [db _]
   (:notifications db)))

