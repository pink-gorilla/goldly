(ns goldly.service.core
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warnf error errorf]]
   [cljs.core.async :refer [>! chan close! put!] :refer-macros [go]]
   [promesa.core :as p]
   [reagent.core :as r]
   [modular.ws.core :refer [send!]]
   [frontend.notifications.core :as n]
   [goldly.service.result :refer [update-atom-where]]))

(defn print-result [[event-type data]]
  (warnf "service result rcvd: type: %s data: %s" event-type data))

; run with callback

(defn run-cb [{:keys [fun args timeout cb]
               :or {timeout 120000 ; 2 minute
                    cb print-result}
               :as params}]
  (let [p-clean (dissoc params :cb :a :where)]
    (infof "running service :%s args: %s" fun args)
    (send! [:goldly/service p-clean] cb timeout)
    nil))

(defn clj
  ([fun]
    (clj {} fun))
  ([fun & args]
   (let [[opts fun args] (if (map? fun)
                           [fun (first args) (rest args)]
                           [{} fun args])
         {:keys [timeout] :or {timeout 120000}} opts
         r (p/deferred)
         on-result (fn [msg]
                     (if (= msg :chsk/timeout)
                         (p/reject! r {:msg "timeout"})
                         (let [[_ data] msg
                               {:keys [result error]} data]
                              (if error
                                  (p/reject! r error)
                                  (p/resolve! r result)))))]
     (run-cb {:fun fun :args (into [] args) :timeout timeout :cb on-result})
     r)))

(defn clj-atom [& args]
   (let [a (r/atom {:status :pending})
         rp (apply clj args)]
      (-> rp
         (p/then (fn [data] (swap! a assoc :status :done :data data)))
         (p/catch (fn [error] (swap! a assoc :status :error :error error))))
      a))


; run with core-async channel

(defn run [params]
  (let [ch (chan)
        cb (fn [event]
             (infof "service/run cb: %s" event) +
             (if (= event :chsk/timeout)
               (put! ch {:error :timeout})
               (let [[_ data] event]
                 (put! ch data))))]
    (run-cb (assoc params :cb cb))
    ch))

(defn wait-chan-result [ch fn-success fn-err]
  (go
    (let [{:keys [error result] :as r} (<! ch)]
      (when error
        (fn-err error))
      (when result
        (fn-success result)))))

; run to atom

(defn process-error [data]
  (error "error in clj-service: " data)
  (n/add-notification :error (pr-str data)))

(defn process-timeout [data]
  (let [data-safe (dissoc data :a)]
    (error "timeout in clj-service: " data-safe)
    (n/add-notification :error (str "timeout clj-fun: " (:fun data)))))

; run-a-map has identical syntax to run-cb, except it
; has a and path as well. this is needed so we can pass timeout.

(defn run-a-map [{:keys [a path] :as args}]
  (let [on-result (fn [r]
                    (if (= r :chsk/timeout)
                      (process-timeout args)
                      (let [;_ (info "run-a-map-cb: " r) ; [:goldly/service {:fun :demo/add, :args (2 7), :result 9}]
                            [_ data] r ; [:goldly/service {:result :error}]
                            {:keys [result error]} data]
                        (if error
                          (process-error data)
                          (update-atom-where a path result)))) )]
    (run-cb (merge
             (dissoc args :a :path)
             {:cb on-result}))))

; legacy run-a

(defn run-a [a path fun & args]
  (run-a-map {:a a
              :path path
              :fun fun
              :args args}))
