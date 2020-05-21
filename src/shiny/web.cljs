(ns shiny.web
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
   [reagent.core :as r]
   [reagent.dom]
   [re-frame.core :refer [dispatch clear-subscription-cache! subscribe]]
   ;[pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   ; add dependencies of this project to bundle
   ;[pinkgorilla.ui.default-renderer]
   [shiny.core]
   [shiny.ws :refer [send! start-router!]]
   [shiny.events] ; add reframe event handlers
   [shiny.subs]))

(enable-console-print!)

(timbre/set-level! :trace)
;(timbre/set-level! :debug)
;(timbre/set-level! :info)

(defn hook-browser-navigation!
  []
  (doto (History.)
    (events/listen EventType/NAVIGATE (fn [event]
                                        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defonce history (hook-browser-navigation!))

(defn   nav!
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
    (dispatch [:shiny/nav :info]))
  (defroute "/system" [id]
    (println "nav: /system " id)
    (dispatch [:shiny/nav :system id])
    (dispatch [:shiny/send :shiny/system id])
))

(defn infos []
  (let [ids (subscribe [:systems])
        _ (println "info: systems: " @ids)]
    [:<>
     [:h1 "running system info: " (count @ids)]
     (for [id @ids]
       [:a {:href (str "#/system?id=" id)} id])]))

(defn system [id]
  [:h1 "system: " id])


(defn nav []
  [:nav.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "Shiny clj"]]
    [:div#navbar.collapse.navbar-collapse
     [:ul.nav.navbar-nav
      [:li
       [:a {:href "#/info"} "Info"]]
      [:li
       [:a {:href "#/system"} "system"]]]]]])

(defn app []
  (let [main (subscribe [:main])
        id (subscribe [:system-id])]
    [:div.container
     [nav]
     (case @main
       :info [infos]
       :system [system @id]
       [infos])]))

(defn mount-app []
  (reagent.dom/render [app]
                      (.getElementById js/document "app")))

 ; (secretary/dispatch! route))
; 


;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load before-reload []
  (println "shadow-cljs reload: before")
  (info "shadow-cljs reload: before"))

(defn ^:dev/after-load after-reload []
  (enable-console-print!)
  (timbre/set-level! :debug)
  (println "shadow-cljs reload: after")
  (info "shadow-cljs reload: after")

  (println "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  (println "re-loading configuration from server..")
  ;(dispatch [:load-config])

  (app-routes)
  (start-router!)
  (println "mounting notebook-app ..")
  (mount-app))


(after-reload)