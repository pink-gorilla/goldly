(ns demo.demo1
  (:require
   [goldly.core :as goldly]
   [goldly.web :as shinyweb]
   ; systems
   [systems.click-counter :refer [click-counter]]
   [systems.greeter :refer [greeter]]
   [systems.controls :refer [controls]]
   [systems.holiday-destinations :refer [holiday-destinations]])
  (:gen-class))


(defn -main []
  (shinyweb/server-start! {:port 8000})
  
  (goldly/system-start! click-counter)
  (goldly/system-start! greeter)
  (goldly/system-start! controls)
  (goldly/system-start! holiday-destinations)
  
  (println "running systems: " (goldly/systems-response))
 ; 
  )




