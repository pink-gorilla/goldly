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


(defmacro system [ [system-cljs system-clj] ]
  (let [id# (gensym "custom-eval")]
    {:id id#
     :cljs (pr-str `system-cljs)
     :clj system-clj}))

(defn system-start!
  [system]
  (system->clj system) 
  )
