(ns goldly.service.core
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof warn error errorf]]
   [modular.ws.core :refer [send! send-all! send-response]]
   [modular.ws.msg-handler :refer [-event-msg-handler send-reject-response]]
   [modular.permission.service :refer [add-permissioned-services]]
   [modular.permission.app :refer [service-authorized?]]
   ))

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
           {:error (str "Exception: "
                        "data:" (pr-str (ex-data e))  ;(pr-str e)
                        "msg:" (pr-str (ex-message e))
                        )}))
    {:error (str "service not found: " kw)}))

(defn create-clj-run-response [{:keys [fun args] :as params}]
  (let [result (if args
                 (apply run fun args)
                 (run fun))]
    (merge params result)))

(defn run-service [req {:keys [fun args] :as params}]
  (try
    (infof "%s %s" fun (into [] args))
    (let [response (create-clj-run-response params)]
      (if (:error response)
        (errorf "service fn: %s error: %s" (:fun response) (:error response))
        (debug "sending service response: " response))
      (send-response req :goldly/service response))
    (catch Exception e
       (error "exception in run-service fun:" fun " ex: " e)
      )
    ))

(defmethod -event-msg-handler :goldly/service
  [{:keys [event id ?data uid] :as req}]
  (let [[_ params] event ; _ is :goldly/service
        {:keys [fun args]} params]
    (if (service-authorized? fun uid)
      (future
        (run-service req params))
      (send-response req :goldly/service  {:error "Not Permissioned"}))))
