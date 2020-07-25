(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info]]
   [org.httpkit.server :as httpkit]
   [webly.config :refer [webly-config]]
   [webly.web.handler :refer [make-handler]]
   [goldly.web.ws :refer [start-router!]]
   [goldly.puppet.loader :refer [load-components-namespaces require-components]]
   [goldly.puppet.db :refer [systems-response]]
   [goldly.web.routes :refer [goldly-routes-backend goldly-routes-frontend]]
   [goldly.web.handler] ; side-effects
   )
  (:gen-class))


(info "making handler ..")
(def handler (make-handler goldly-routes-backend goldly-routes-frontend))

(defn start-httpkit-server!
  "starts webserver with websockets"
  [options]
  (println "starting web at " (:port options))
  (httpkit/run-server handler {:port (:port options)})
  (start-router!))


(defn goldly-run!
  "This starts goldly (web server, user defined systems,...)"
  [{:keys [port
           app-systems-ns
           user-systems-dir]
    :or {port 9000
         ;app-systems-dir "./src/systems/"
         app-systems-ns '[systems.help
                          systems.components
                          systems.snippets
                          systems.login]}}]
  ;(system-start! components)
  (when app-systems-ns
    (println "loading app systems from: " app-systems-ns)
    ;(load "address_book/core") ; this works with classpath
    (load-components-namespaces app-systems-ns))
  (when user-systems-dir
    (println "loading user systems from: " user-systems-dir)
    (require-components user-systems-dir))
  (start-httpkit-server! {:port port}))

(defn -main [& args]
  (println "goldly app starting with cli-args: " args)
  
  (swap! webly-config assoc :timbre-loglevel :info)
  (swap! webly-config assoc :title "goldly")
  (swap! webly-config assoc :start "goldy.app.start (); ")

  (let [user-systems-dir (first args)]
    (goldly-run! {:user-systems-dir user-systems-dir}))
  (println "goldly started successfully. systems running: " (systems-response)))

(comment
  (goldly-run! {})
  (-main)

 ;
  )