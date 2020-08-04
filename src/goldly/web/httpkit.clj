(ns goldly.web.httpkit
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [org.httpkit.server :as httpkit]
   [goldly.web.ws :refer [start-router!]]))

(defn start-httpkit-server!
  "starts webserver with websockets"
  [handler options]
  (info "starting httpkit web at " (:port options))
  (httpkit/run-server handler {:port (:port options)})
  (start-router!))