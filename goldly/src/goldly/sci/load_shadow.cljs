(ns goldly.sci.load-shadow
  (:require
   [cljs.core.async :refer [>! <! chan close! put! take!] :refer-macros [go]]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [shadow.lazy :as lazy]))

(defn load-ext-shadow [module loadable]
  (info "load-ext-shadow .. " loadable)
  (js/Promise.
   (fn [resolve reject]
     (let [handle-load (fn [mod]
                         (error "shadow module : " module "did load: " mod)
                         (let [mod-js (clj->js mod)]
                           (when-let [joke (:joke mod)]
                             (println "joke: " (joke)))
                           (when-let [add (:add mod)]
                             (println "adding: " (add 7 7)))
                           (resolve mod-js)))]
       (lazy/load loadable handle-load)))))