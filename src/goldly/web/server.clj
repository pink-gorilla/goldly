(ns goldly.web.server
  (:require
   [clojure.string]
   [org.httpkit.server :as httpkit]
   [goldly.web.handler :refer [default-handler]]
   [goldly.web.ws :refer [start-router!]]))

(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [options]
  (println "starting web at " (:port options))
  (httpkit/run-server default-handler {:port (:port options)}) ; (handler/site app-routes)  
  (start-router!)
  (reset! server nil))