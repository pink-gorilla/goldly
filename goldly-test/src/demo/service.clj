(ns demo.service
  (:require
   [taoensso.timbre :as timbre :refer [info warn errorf]]
   [goldly.service.core :as s]))

(info "namespace demo.service is getting loaded...")

(defn fun-add [a b]
  (+ a b))

(defn fun-quote []
  "The early fish catches the worm.")

(defn fun-ex []
  (throw (Exception. "something bad happened")))

(s/add {:demo/add fun-add
        :demo/quote fun-quote
        :demo/ex fun-ex})