(ns goldly.scratchpad
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.core.async :refer [go chan >!]]
   [reval.type.converter :refer [value->hiccup]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]))

(defn clear! []
  (info "clearing scratchpad: ")
  (send-all! [:scratchpad/msg {:op :clear}])
  nil)

(defn ->hiccup [h]
  (if (vector? h)
    h
    (value->hiccup h)))

(defn show! [h-or-type]
  (let [h (->hiccup h-or-type)]
    (info "sending to scratchpad: " h)
    (send-all! [:scratchpad/msg {:op :show
                                 :hiccup h
                                 :ns (str *ns*)}])
    h))

(defn show-as [ui-kw & args]
  (let [h (into [ui-kw] args)]
    (show! h)
    h))

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
  (show! [:p "hello, scratchpad!"])

  (clear!)
;  
  )