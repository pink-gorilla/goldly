(ns shiny.web
  (:require-macros [secretary.core :refer [defroute]])
  (:import
   [goog History]
    ;; [goog.history Html5History]
   )
  (:require
   [cljs.pprint]
   [reagent.core :as r]
   [reagent.dom]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]

   [pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   ; add dependencies of this project to bundle
   [pinkgorilla.ui.default-renderer]
   [shiny.core]))

(enable-console-print!)

(timbre/set-level! :debug)
;(timbre/set-level! :info)

(defn hook-browser-navigation!
  []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defonce history (hook-browser-navigation!))

(defn   nav!
  "navigates the browser to the url. 
   Triggers secretary route events"
  [url]
  (.setToken history url))


(defonce current (reagent.core/atom {:page :info
                                     :system nil}))

(defn app-routes
  [& [{:keys [hook-navigation]
       :or   {hook-navigation false}}]]
  (info "Hook navigation" hook-navigation)
  (secretary/set-config! :prefix "#")
  (defroute "/info" []
    (println "/info")
    (swap! current assoc :page :info))
  (defroute "/system" [id]
    (println "/system")
    (swap! current assoc :page :system
           :system id)))

(defn infos []
  [:h1 "infos"])

(defn system [id]
  [:h1 "system: " id])


(defn app []
  (fn []
    (let [current @current
          id (:system current)]
      [:<>
       [:h1 "shining.."]
       (case (:type current)
         :info [infos]
         :system [system id])])))

(defn stop []
  (js/console.log "Stopping..."))

(defn start []
  (js/console.log "Starting...")
  ;(js/console.log (print-registered-tags))
  (app-routes)
  (nav! "/info")
  (println "B")
  (reagent.dom/render [app] ; (tag-inject app)
                      (.getElementById js/document "app"))
  (println "C")
  ;(if (not route)

   ; (secretary/dispatch! route))
  )



(defn ^:export init []
  (start))