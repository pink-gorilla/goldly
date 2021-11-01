(ns goldly.scratchpad.core
  (:require
   [clojure.string]
   [clojure.core.async :refer [go chan >!]]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send-all! send! send-response watch-conn]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]))

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
  (send-all! [:goldly/scratchpad-set {:type type :src src}]))