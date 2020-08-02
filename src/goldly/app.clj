(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info]]
   [org.httpkit.server :as httpkit]
   [webly.config :refer [webly-config]]
   [webly.web.handler :refer [make-handler]]
   [goldly.web.ws :refer [start-router!]]
   [goldly.web.ws-handler] ; side-effects
   [goldly.puppet.loader :refer [load-components-namespaces require-components]]
   [goldly.puppet.db :refer [systems-response]]
   [goldly.web.routes :refer [goldly-routes-backend goldly-routes-frontend]])
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
  [{:keys [timbre-loglevel
           port
           app-systems-ns
           user-systems-dir]
    :or {timbre-loglevel :debug ;  :info
         port 9000
         ;app-systems-dir "./src/systems/"
         app-systems-ns '[systems.help
                          systems.components
                          ;systems.snippets
                          systems.login
                          systems.greeter
                          systems.click-counter]}}]

  (swap! webly-config assoc :timbre-loglevel timbre-loglevel)
  (swap! webly-config assoc :title "goldly")
  (swap! webly-config assoc :start "goldly.app.start();")
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
  (println "goldly starting with cli-args: " args)
  (let [user-systems-dir (first args)]
    (goldly-run! {:timbre-loglevel :info
                  :user-systems-dir user-systems-dir}))
  (println "goldly started successfully. systems running: " (systems-response)))

(comment
  (goldly-run! {})
  (-main)

 ;
  )