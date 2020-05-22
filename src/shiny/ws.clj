(ns shiny.ws
  (:require
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [clojure.java.io :as io]
   ;[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   ;[ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params]
   [ring.middleware.params]
;   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
  ; [ring.middleware.anti-forgery :as af :refer :all]
   [ring.middleware.anti-forgery :refer (*anti-forgery-token*)]
   [ring.util.response :as response]
   [compojure.core :as comp :refer (defroutes GET POST)]
   [taoensso.encore :as encore :refer (have have?)]
   [taoensso.timbre :as log :refer (tracef debugf infof warnf errorf)]
   [taoensso.sente  :as sente]
   [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
   [taoensso.sente.packers.transit :as sente-transit]
   [cheshire.core :as json]))

(defn unique-id
  "Get a unique id for a session."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))


;(log/set-level! :info)
;; (log/set-level! :debug)
;(taoensso.timbre/set-level! :trace) ; Uncomment for more logging
(taoensso.timbre/set-level! :debug)

(reset! sente/debug-mode?_ true) ; Uncomment for extra debug info

; packer :edn

(let [packer (sente-transit/get-transit-packer)
      chsk-server (sente/make-channel-socket-server!
                   (get-sch-adapter)
                   {:packer packer
                    :csrf-token-fn nil ; awb99; disable CSRF checking.
                    })
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
    (debugf "Broadcasting %s to %s clients" (first data) (count uids))
    (doseq [uid uids]
      (chsk-send! uid data))))


(defroutes ws-handler
  (GET "/token" req (json/generate-string {:csrf-token *anti-forgery-token*}))
  (GET  "/chsk" req
    (debugf "/chsk got: %s" req)
    (let [r (ring-ajax-get-or-ws-handshake req)]
      (println "ws init: " r)
      (println "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
      r))
  (POST "/chsk" req (ring-ajax-post req)))


;; Router

(defmulti -event-msg-handler :id)

(defn event-msg-handler [{:keys [id ?data event] :as ev-msg}]
  (tracef "Event: %s" event)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (tracef "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defonce router_ (atom nil))
(defn stop-router! [] (when-let [stop-fn @router_] (stop-fn)))

(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router! ch-chsk event-msg-handler)))

;; Heartbeat sender

(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 30000))
    (when @broadcast-enabled?_ (send-all! [:shiny/heartbeat {:i i}]))
    (recur (inc i))))

;(start-heartbeats!)

(comment
  (println "clients: " @connected-uids)

  ;(start-heartbeats!)
  (send-all! [:pinkie/broadcast {:a 13}])

  ;
  )