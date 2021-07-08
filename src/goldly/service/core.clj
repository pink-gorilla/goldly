(ns goldly.service.core
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof warn error errorf]]
   [webly.ws.core :refer [send! send-all! send-response]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]))

(def services-atom (atom {})) ; {:cookie/get db/get-cookie}

(defn add [m]
  (swap! services-atom merge m))

(defn services-list []
  (keys @services-atom))

(defn run [kw & args]
  (if-let [fun (kw @services-atom)]
    (try {:result (if args
                    (apply fun args)
                    (fun))}
         (catch clojure.lang.ExceptionInfo e
           {:error (str "Exception: " (pr-str e))})
         (catch Exception e
           {:error (str "Exception: " (pr-str e))}))
    {:error (str "service not found: " kw)}))

(defn create-clj-run-response [{:keys [fun args] :as params}]
  (infof "running: %s args: %s" fun (into [] args))
  (let [result (if args
                 (apply run fun args)
                 (run fun))]
    (merge params result)))

(defmethod -event-msg-handler :goldly/service
  [{:as ev-msg :keys [event id ?data]}]
  (let [[_ params] event ; _ is :goldly/service
        response (create-clj-run-response params)]
    (if (:error response)
      (errorf "service fn: %s error: %s" (:fun response) (:error response))
      (debug "sending service response: " response))
    (send-response ev-msg :goldly/service response)))
