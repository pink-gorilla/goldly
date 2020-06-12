(ns goldly.web.routes-old
  (:require-macros [secretary.core :refer [defroute]])
  (:import
   [goog History]
    ;; [goog.history Html5History]
   )
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [secretary.core :as secretary]
   [reagent.dom]
   [re-frame.core :refer [dispatch]]))

(defn hook-browser-navigation!
  []
  (doto (History.)
    (events/listen EventType/NAVIGATE (fn [event]
                                        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defonce history (hook-browser-navigation!))

(defn nav!
  "navigates the browser to the url. 
   Triggers secretary route events"
  [url]
  (.setToken history url))

(defn app-routes
  [& [{:keys [hook-navigation]
       :or   {hook-navigation false}}]]
  (info "Hook navigation" hook-navigation)
  (secretary/set-config! :prefix "#")
  (defroute "/info" []
    (println "nav: /info")
    (dispatch [:goldly/nav :info]))
  (defroute "/system/:id" [id query-params]
    (println "nav: /system " id)
    (dispatch [:goldly/nav :system id])))