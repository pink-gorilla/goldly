(ns goldly.web
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
   [re-frame.core :refer [dispatch dispatch-sync clear-subscription-cache! subscribe]]

   ; the following just adds dependencies to bundle
   [pinkgorilla.ui.default-setup] ; renderable-ui
   [pinkgorilla.ui.default-renderer] ; gorilla-ui 
   [pinkgorilla.ui.gorilla-plot.pinkie] ; gorilla-plot
   ;[pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   
   [goldly.web.ws :refer [send! start-router!]]
   [goldly.system :refer [render-system]]
   [goldly.events] ; add reframe event handlers
   [goldly.subs]
   
   ))

(defn print-log-init! []
  (enable-console-print!)
;(timbre/set-level! :trace) ; Uncomment for more logging
  (timbre/set-level! :debug)
  #_(timbre/set-level! :info))

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

(defn infos []
  (let [systems (subscribe [:systems])
        _ (println "info: systems: " @systems)]
    [:<>
     [:h1 "running systems: " (count @systems)]
     [:ul
      (for [{:keys [id name]} @systems]
        ^{:key id}
        [:li.m-3
         [:a {:class "m-3 bg-yellow-300"
              :href (str "#/system/" id)} name]])]]))

(defn error-boundary [_ #_comp]
  (let [error (r/atom nil)
        info (r/atom nil)]
    (r/create-class
     {:component-did-catch (fn [_ #_this _ #_e i]
                             (reset! info i))
      :get-derived-state-from-error (fn [e]
                                      (reset! error e)
                                      #js {})
      :reagent-render (fn [comp]
                        (if @error
                          [:div "Something went wrong."
                           [:p (str @error)]]
                          comp))})))

(defn system
  "requests system with id from server
   and displays it."
  [id]
  (info "showing system: " id)
  (dispatch-sync [:goldly/system-store id nil])
  (dispatch [:goldly/send :goldly/system id])
  (let [system (subscribe [:system])]
    (fn []
      [:<>
       [:a {:class "m-2 bg-blue-200 border-dotted border-orange-400"
            :href "#/info"} "Systems"]
       (if (nil? @system)
         [:h1 "loading .."]
         [:<>
          [:h1.bg-orange-300 (str (:name @system) " " id)]
          [error-boundary
           [render-system (merge {:id (:id @system)}
                                 (:cljs @system)
                                 {:fns-clj (:fns-clj @system)})]]])])))

(defn app []
  (let [main (subscribe [:main])
        id (subscribe [:system-id])]
    [:div ;.w.container
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
  (print-log-init!)
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