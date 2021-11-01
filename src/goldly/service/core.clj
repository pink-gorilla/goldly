(ns goldly.service.core
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof warn error errorf]]
   [webly.ws.core :refer [send! send-all! send-response]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]))

;; services registry

(def services-atom (atom {})) ; {:cookie/get db/get-cookie}

(defn add [m]
  (swap! services-atom merge m))

(defn services-list []
  (keys @services-atom))

(defn get-fn [kw]
  (cond
    (keyword? kw) (kw @services-atom)
    (symbol? kw) (resolve kw)))

; (get-fn :ff)
; (get-fn 'get-collections)

(defn run [kw & args]
  (if-let [fun (get-fn kw)]
    (try {:result (if args
                    (apply fun args)
                    (fun))}
         (catch clojure.lang.ExceptionInfo e
           {:error (str "Exception: " (pr-str e))})
         (catch Exception e
           {:error (str "Exception: " (pr-str e))}))
    {:error (str "service not found: " kw)}))

(defn create-clj-run-response [{:keys [fun args] :as params}]
  (infof "%s %s" fun (into [] args))
  (let [result (if args
                 (apply run fun args)
                 (run fun))]
    (merge params result)))

(defn run-service [{:keys [fun args] :as params}]
  (let [response (create-clj-run-response params)]
    (if (:error response)
      (errorf "service fn: %s error: %s" (:fun response) (:error response))
      (debug "sending service response: " response))
    response))

(defmethod -event-msg-handler :goldly/service
  [{:as ev-msg :keys [event id ?data]}]
  (let [[_ params] event ; _ is :goldly/service
        response (run-service params)]
    (send-response ev-msg :goldly/service response)))
