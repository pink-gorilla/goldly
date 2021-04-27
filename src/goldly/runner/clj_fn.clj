(ns goldly.runner.clj-fn
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [webly.ws.core :refer [send! send-all! send-response]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.puppet.db :refer [get-system]]))

(defn run-system-fn-clj [id fun-kw args]
  (infof "run-system-fn-clj system %s fun: %s" id fun-kw)
  (let [system (get-system (keyword id))]
    (if system
      (let [fun-vec (get-in system [:clj :fns fun-kw])]
        (if fun-vec
          (let [[fun where] fun-vec]
            (infof "system %s executing fun: %s args: %s" id fun-kw args)
            {:result (if args
                       (apply fun args)
                       (fun))
             :where where})
          (do (errorf "system %s : fn not found: %s" id fun-kw)
              (error "system: " system)
              {:error (str "function not found: " fun-kw)})))
      (do (infof "system %s : system not found. fn: %s" id fun-kw)

          {:error (str "system " id "not found, cannot execute function: " fun-kw)}))))

(defn create-clj-run-response [run-id id fun-kw args]
  (let [result (try (run-system-fn-clj id fun-kw args)
                    (catch clojure.lang.ExceptionInfo e
                      {:error (str "Exception: " (pr-str e))})
                    (catch Exception e
                      {:error (str "Exception: " (pr-str e))}))]
    (merge {:run-id run-id
            :system-id id
            :fun fun-kw} result)))

(defmethod -event-msg-handler :goldly/dispatch
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [[event-name [run-id system-id fun & args]] event]
    (infof "rcvd %s runner: %s system: %s fun: %s args: %s" event-name run-id system-id fun args)
    (let [response (create-clj-run-response run-id system-id fun args)]
      (debug "sending clj response: " response)
      (send-response ev-msg :goldly/dispatch response))))

(defn broadcast-to-system [system-id data path]
  (send-all! [:goldly/clj-result {:run-id nil
                                  :system-id system-id
                                  :fun nil
                                  :result data
                                  :where path}]))

#_(defn update-state! [system-id {:keys [result where] :as update-spec}]
    (let [response (merge {:run-id nil
                           :system-id system-id
                           :fun nil} update-spec)]
      (info "sending " response)
      (send-all! [:goldly/dispatch response])))
