(ns goldly.app
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [reagent.dom]
   [re-frame.core :refer [clear-subscription-cache! subscribe]]

   ; the following just adds dependencies to bundle
   [pinkgorilla.ui.default-setup] ; renderable-ui
   [pinkgorilla.ui.default-renderer] ; gorilla-ui 
   [pinkgorilla.ui.gorilla-plot.pinkie] ; gorilla-plot
   ;[pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   
   [goldly.web.ws :refer [start-router!]]
   [goldly.events] ; add reframe event handlers
   [goldly.puppet.subs]
   [goldly.puppet.db]
   [goldly.puppet.loader :refer [system]] 
   [goldly.puppet.nav :refer [app-routes]]
   ))

(defn print-log-init! []
  (enable-console-print!)
;(timbre/set-level! :trace) ; Uncomment for more logging
  (timbre/set-level! :debug)
  #_(timbre/set-level! :info))


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