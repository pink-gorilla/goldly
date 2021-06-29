(ns goldly.broadcast.core
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [webly.ws.core :refer [send! send-all! send-response]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.puppet.db :refer [get-system]]))

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