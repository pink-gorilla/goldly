(ns goldly.service.core
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :refer-macros [trace debug debugf info infof warnf errorf]]
   [webly.ws.core :refer [send!]]
   [goldly.service.result :refer [update-atom-where]]))

(defn print-result [[event-type data]]
  (warnf "service result rcvd: type: %s data: %s" event-type data))

(defn update-atom-result [a path [event-type data]]
  (warnf "service result rcvd: type: %s data: %s" event-type data))

(defn run [{:keys [fun args timeout cb]
            :or {timeout 5000
                 cb print-result}
            :as params}]
  (let [p-clean (dissoc params :cb :a :where)]
    (infof "running service :%s args: %s" fun args)
    (send! [:goldly/service p-clean] cb timeout)
    nil))

(defn run-a [a path fun & args]
  (let [on-result (fn [[event-type data]]
                    (let [{:keys [result error]} data]
                      (if error
                        (error "error in clj-service: " data)
                        (update-atom-where a path result))))]
    (run {:fun fun :args args :cb on-result})))
