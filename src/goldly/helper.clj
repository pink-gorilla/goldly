(ns goldly.helper
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send!]]))

(defn send-ws-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn send-fn]}
                        goldly-tag
                        response]
  (let [session (:session ring-req)
        uid (:uid session)]
    (when (nil? ?reply-fn)
      (error "reply-fn is nil. the client did chose to use messenging communication istead of req-res communication."))
    (if (nil? uid)
      (error "ws request uid is nil. ring-session not configured correctly.")
      (info "uid: " uid))
    (if response
      (cond
        ?reply-fn (?reply-fn [goldly-tag response])
        uid (send! uid [goldly-tag response])
        :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
      (error "Can not send ws-response for nil response. " goldly-tag))))