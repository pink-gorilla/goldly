(ns demo.demo1
  (:require
   [goldly.core :as goldly]
   [goldly.web :as shinyweb]
   ; systems
   [systems.click-counter :refer [click-counter]]
   [systems.greeter :refer [greeter]]
   [systems.controls :refer [controls]]
   [systems.holiday-destinations :refer [holiday-destinations]]
   ;[systems.r-telephone :refer [r-telephone]]
   [systems.r-quakes :refer [r-quakes]]
   )
  (:gen-class))


(defn -main []
  (shinyweb/server-start! {:port 8000})
  
  (goldly/system-start! click-counter)
  (goldly/system-start! greeter)
  (goldly/system-start! controls)
  (goldly/system-start! holiday-destinations)
  ;(goldly/system-start! r-telephone)
  (goldly/system-start! r-quakes)
  
  
  (println "running systems: " (goldly/systems-response))
 ; 
  )




