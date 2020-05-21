(ns shiny.web
  (:require
   [clojure.string]
   [shiny.core]))

;; webserver

(defn routes []
  {:status "running system stats"
   :systems "id or route-name parameter displays system"}
  
  )

(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [options]
  (println "starting at " (:port options))
  (reset! server nil))