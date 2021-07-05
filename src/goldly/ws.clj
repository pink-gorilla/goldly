(ns goldly.ws
  (:require
   [clojure.string]
   [clojure.core.async :refer [go chan >!]]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send-all! send! send-response watch-conn]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.system.db :refer [system-response systems-response]]))

(defmethod -event-msg-handler :goldly/systems
  [ev-msg]
  (let [response (systems-response)]
    (send-response ev-msg :goldly/systems response)))

(defmethod -event-msg-handler :goldly/system
  [{:as ev-msg :keys [event]}]
  (let [[event-name system-id] event]
    (let [response (or (system-response system-id) {:id system-id
                                                    :status :g/system-nil})
          _ (debug "sending system-response: " response)]
      (send-response ev-msg :goldly/system response))))

; on connection

(defn on-connect-send-systems [old new]
  (let [uids (:any new)]
    (info "uids connected: " uids)
    (doseq [uid uids]
      (info "sending systems info to: " uid)
      (send! uid (systems-response)))))

(watch-conn on-connect-send-systems)

(defn scratchpad-get []
  (send-all! [:goldly/scratchpad-get]))

(defonce chan-scratchpad-get (chan))

(defmethod -event-msg-handler :goldly/scratchpad
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (go (info "scratchpad data rcvd: " data)
        (>! chan-scratchpad-get data))
    nil))

(defn scratchpad-set [type src]
  (send-all! [:-set {:type type :src src}]))