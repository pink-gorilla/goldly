(ns goldly.web.ws
  (:require
   [clojure.string :as str]
   [cljs.core.async :as async  :refer (<! >! put! chan)]
   [re-frame.core :refer [reg-event-db dispatch-sync dispatch]]
   [taoensso.encore :as encore :refer-macros (have have?)]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf trace)]
   [taoensso.sente :as sente :refer (cb-success?)]
   [taoensso.sente.packers.transit :as sente-transit] ;; Optional, for Transit encoding
   )
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)]))

;; CSRF check

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

;; see: https://github.com/ptaoussanis/sente/blob/master/example-project/src/example/client.cljs

(defn sente-csrf-warning []
  (if ?csrf-token
    (println "CSRF token detected in HTML, great!")
    (println "CSRF token NOT detected in HTML, default Sente config will reject requests")))

(defn- log [a-thing]
  (.log js/console a-thing))

(debugf "connecting sente websocket..")

(let [packer (sente-transit/get-transit-packer)
      {:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket-client!
                                            "/chsk" ; Must match server Ring routing URL
                                            ?csrf-token
                                            {:type :auto  ; :ajax
                                             :packer packer})]
  (def chsk chsk)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

(defmulti -event-msg-handler :id)

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  ;(debugf "Event: %s" event)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (debugf "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (if (:first-open? new-state-map)
      (dispatch [:goldly/ws-open new-state-map])
      (debugf "Channel socket state change: %s" ?data))))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (debugf "Handshake: %s" ?data)))

;; This is the main event handler; If we want to do cool things with other kinds of data 
;; going back and forth, this is where we'll inject it.
(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (let [[id msg] ?data]
    (dispatch [:goldly/event id msg])))

;;;; Sente event router (our `event-msg-handler` loop)

(defonce router_ (atom nil))
(defn  stop-router! [] (when-let [stop-f @router_] (stop-f)))

(defn start-router! []
  (stop-router!)
  (sente-csrf-warning)
  (reset! router_
          (sente/start-client-chsk-router! ch-chsk event-msg-handler)))

(defn send! [data]
  (chsk-send! data 5000
              (fn [cb-reply]
                (debugf "Callback reply: %s" cb-reply))))


;; Heartbeat sender


(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 30000))
    (when @broadcast-enabled?_ (send! [:goldly/systems {:i i}]))
    (recur (inc i))))

(start-heartbeats!)