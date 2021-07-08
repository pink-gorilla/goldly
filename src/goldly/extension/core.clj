(ns goldly.extension.core
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.writer :refer [write-status]]
   [webly.config :refer [config-atom]]
   [goldly.extension.pinkie :refer [pinkie-atom]]))

;; config
(defn lazy-enabled []
  (or (get-in @config-atom [:goldly :lazy]) false))

(defn lazy-excludes []
  (or (get-in @config-atom [:goldly :lazy-exclude]) #{}))

(defn lazy-excluded? [module-name]
  (let [excludes (lazy-excludes)]
    (contains? excludes module-name)))

(defonce extension-atom (atom {}))
(defn save-extensions [extensions]
  (reset! extension-atom extensions)
  (write-status "extensions" extensions))
(defn save-pinkie []
  (write-status "pinkie" @pinkie-atom))

; (defn lazy? [ext-name])
