(ns goldly.runner
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [webly.ws.core :refer [send-all! send!]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.puppet.db :refer [get-system add-system]]
   [goldly.system :refer [system->cljs]]
   [goldly.runner.clj-fn :refer [create-clj-run-response]]))

;; system


(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type event-name :args args}]
    (send-all! [:goldly/event message])))

(defn dispatch [system-id event-name & args]
  (println "dispatching " system-id event-name)
  (send-event system-id event-name args))

(defmethod -event-msg-handler :goldly/dispatch
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name [run-id system-id fun & args]] event]
    (infof "rcvd %s runner: %s system: %s fun: %s args: %s" event-name run-id system-id fun args)
    (let [response (create-clj-run-response run-id system-id fun args)
          _ (debug "response: " response)]
      (if ?reply-fn
        (?reply-fn response)
        (send! uid [:goldly/dispatch response])))))

(defn update-state! [system-id {:keys [result where] :as update-spec}]
  (let [response (merge {:run-id nil
                         :system-id system-id
                         :fun nil} update-spec)]
    (info "sending " response)
    (send-all! [:goldly/dispatch response])))

(defn system-start!
  [system]
  (info "starting system " (:id system))
  (add-system system)
  (system->cljs system))





