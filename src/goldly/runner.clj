(ns goldly.runner
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [webly.ws.core :refer [send-all! send!]]
   [goldly.puppet.db :refer [get-system add-system]]
   [goldly.system :refer [system->cljs]]))

;; system


(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type event-name :args args}]
    (send-all! [:goldly/event message])))

(defn dispatch [system-id event-name & args]
  (println "dispatching " system-id event-name)
  (send-event system-id event-name args))

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





