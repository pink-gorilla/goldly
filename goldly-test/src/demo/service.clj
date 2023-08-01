(ns demo.service
  (:require
   [taoensso.timbre :as timbre :refer [debug]]))

(debug "namespace demo.service is getting loaded...")

(defn add [a b]
  (+ a b))

(defn quote []
  "The early fish catches the worm.")

(defn quote-slow []
  (Thread/sleep 10000)
  "Born to be wild.")

(defn ex []
  (throw (Exception. "something bad happened")))

