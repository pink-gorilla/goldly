(ns shiny
  (:require 
    [clojure.string]))

(def server (atom nil))

(defn server-start! 
   "starts webserver with websockets"
   [options]
   (println "starting at " (:port options))
   (reset! server nil))

(defn system->clj [system]
  (println "sending system to cljs"))


(def-macro system [system]

)

(defn system-start!
  [system]
  (system->cñj system) 
  )
