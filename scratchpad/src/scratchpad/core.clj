(ns scratchpad.core
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.core.async :refer [go chan >! <!! >!! alts! timeout]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]))

(defn clear! []
  (info "clearing scratchpad: ")
  (send-all! [:scratchpad/msg {:op :clear}])
  nil)

(defn show! [h]
  (info "sending to scratchpad: " h)
  (send-all! [:scratchpad/msg {:op :show
                               :hiccup h}])
  h)

(defn get! []
  (send-all! [:scratchpad/get-state]))

(defonce chan-scratchpad-get (chan))

(defmethod -event-msg-handler :scratchpad/state
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (go (info "scratchpad data rcvd: " data)
        (>! chan-scratchpad-get data))
    nil))

(comment

  (clear!)

  (show! [:p "hello, scratchpad!"])

;  
  )