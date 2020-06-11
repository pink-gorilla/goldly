(ns goldly.puppet.db
  "runs goldly systems"
  (:require
   [clojure.string]
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [taoensso.timbre :as log :refer (tracef debug debugf info infof warnf error errorf)]
   [goldly.web.ws :refer [send-all! chsk-send! -event-msg-handler connected-uids]]
   [goldly.system :refer [system->cljs]]))

(def systems (atom {}))

(defn get-system [id]
  (get @systems id))

(defn add-system [system]
  (swap! systems assoc (keyword (:id system)) system))

(defn systems-response []
  (let [;_ (println "systems-response: " @systems)
        summary (into []
                      (map (fn [[k v]]
                             {:id (name k)
                              :name (or (:name v) "")}) @systems))
        ;ids (keys @systems)
        ;ids (into [] (map name ids))
        ]
    [:goldly/systems #_ids summary]))


(defmethod -event-msg-handler :goldly/systems
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (tracef "systems event: %s" event)
    (when ?reply-fn
      (?reply-fn (systems-response)))))

(defn system-response
  "gets system to be sent to clj"
  [id]
  (let [id (keyword id)
        system (id @systems)]
    (when system
      (system->cljs system))))


(defmethod -event-msg-handler :goldly/system
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name system-id] event]
    (infof "rcvd  %s %s" event-name system-id)
    (let [response (system-response system-id)]
      (if response
        (if ?reply-fn
          (?reply-fn response)
          (chsk-send! uid [:goldly/system response]))
        (info ":goldly/system request for unknown system: " system-id)))))


(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
               (infof "Connected uids change: %s" new)
               (let [uids (:any new)]
                 (info "uids: " uids)
                 (doseq [uid uids]
                   (info "sending systems info to: " uid)
                   (chsk-send! uid (systems-response)))))))

; heartbeats on ws

(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 60000))
    (when @broadcast-enabled?_ (send-all! (systems-response)))
    (recur (inc i))))

;(start-heartbeats!)
;