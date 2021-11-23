(ns goldly.service.core
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warnf error errorf]]
   [cljs.core.async :refer [>! chan close! put!] :refer-macros [go]]
   [webly.ws.core :refer [send!]]
   [webly.user.notifications.core :as n]
   [goldly.service.result :refer [update-atom-where]]))

(defn print-result [[event-type data]]
  (warnf "service result rcvd: type: %s data: %s" event-type data))

(defn run-cb [{:keys [fun args timeout cb]
               :or {timeout 5000
                    cb print-result}
               :as params}]
  (let [p-clean (dissoc params :cb :a :where)]
    (infof "running service :%s args: %s" fun args)
    (send! [:goldly/service p-clean] cb timeout)
    nil))

(defn run [params]
  (let [ch (chan)
        cb (fn [event] ; _ = event-type ;goldly/service
             (infof "service/run cb: %s" event)
             (let [[_ data] event] ; separate because was throwing exceptions
               (put! ch data)))]
    (run-cb (assoc params :cb cb))
    ch))

(defn process-error [data]
  (error "error in clj-service: " data)
  (n/add-notification :error (pr-str data)))

(defn run-a [a path fun & args]
  (let [on-result (fn [[_ data]] ;  _ = event-type
                    (let [{:keys [result error]} data]
                      (if error
                        (process-error data)
                        (update-atom-where a path result))))]
    (run-cb {:fun fun :args args :cb on-result})))
