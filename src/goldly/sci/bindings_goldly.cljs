(ns goldly.sci.bindings-goldly
  (:require
   [taoensso.timbre :as timbre]))

; why is this here?

; this is a clojurescript namespace
; this functions have to go to the bundel
; goldly has a system to add code to bundels, and this needs to be tested.


(defn sin [x]
  (.sin js/Math x))

(defn log! [l & args]
   ;(timbre/log! l :p args {:?line 77})
  (println l args))

(defn info [& args]
  (apply log! :info args))

(defn warn  [& args]
  (apply log! :warn args))

(defn error  [& args]
  (apply log! :error args))

