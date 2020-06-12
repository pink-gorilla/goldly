(ns goldly.web.ws
  (:require
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [clojure.java.io :as io]
   ;[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   ;[ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params]
   [ring.middleware.params]
;   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
  ; [ring.middleware.anti-forgery :as af :refer :all]
   [ring.util.response :as response]
   [taoensso.encore :as encore :refer (have have?)]
   [taoensso.timbre :as log :refer (tracef debugf info infof warnf error errorf)]
   [taoensso.sente  :as sente]
   [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
   [taoensso.sente.packers.transit :as sente-transit]))

(defn unique-id
  "Get a unique id for a session."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))

(reset! sente/debug-mode?_ true) ; Uncomment for extra debug info

; packer :edn

(defn get-sente-session-uid
  "Get session uuid from a request."
  [req]
  (or (get-in req [:session :uid])
      (unique-id)))

(defn sente-session-with-uid [req]
  (let [session (or (:session req) {})
        uid (or (get-sente-session-uid req)
                (unique-id))]
    (assoc session :uid uid)))

(let [packer (sente-transit/get-transit-packer)
      chsk-server (sente/make-channel-socket-server!
                   (get-sch-adapter)
                   {:packer packer
                    :csrf-token-fn nil ; awb99; disable CSRF checking.
                    :user-id-fn get-sente-session-uid})
      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]} chsk-server]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(defn send-all!
  [data]
  (let [uids (:any @connected-uids)]
    (debugf "Broadcasting event %s to %s clients" (first data) (count uids))
    (doseq [uid uids]
      (when-not (= uid :sente/nil-uid)
        (chsk-send! uid data)))))

;; Router

(defmulti -event-msg-handler :id)

(defn event-msg-handler [{:keys [id ?data event] :as ev-msg}]
  (infof "RCVD: %s" event)
  (if ev-msg
    (-event-msg-handler ev-msg)))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (errorf "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defmethod -event-msg-handler :chsk/uidport-open
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (info ":chsk/uidport-open: %s" ev-msg))

(defmethod -event-msg-handler :chsk/uidport-close
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (info ":chsk/uidport-close: %s" ev-msg))

(defmethod -event-msg-handler :chsk/ws-ping
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (info ":chsk/ws-ping: %s" ev-msg))

(defonce router_ (atom nil))

(defn stop-router! []
  (when-let [stop-fn @router_] (stop-fn)))

(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router! ch-chsk event-msg-handler)))

;; helper fns

(defn send-ws-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn send-fn]}
                        goldly-tag
                        response]
  (let [session (:session ring-req)
        uid (:uid session)]
    (info "?reply-fn: " ?reply-fn  "uid: " uid)
    (if response
      (cond
        ?reply-fn (?reply-fn response)
        uid (chsk-send! uid [:goldly/system response])
        :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
      (error "Can not send ws-response for nil response. " goldly-tag))))


;; Heartbeat sender


(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 30000))
    (when @broadcast-enabled?_ (send-all! [:goldly/heartbeat {:i i}]))
    (recur (inc i))))

;(start-heartbeats!)

(comment
  (println "clients: " @connected-uids)

  ;(start-heartbeats!)
  (send-all! [:pinkie/broadcast {:a 13}])

  ;
  )