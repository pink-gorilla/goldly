(ns goldly.web.handler
  (:require
   [clojure.string]
   [clojure.pprint]
   [taoensso.timbre :as log :refer [info infof]]
   [cheshire.core :as json]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-webly]]
   [goldly.web.ws :refer [get-sente-session-uid sente-session-with-uid
                          ring-ajax-get-or-ws-handshake ring-ajax-post]]))

(defn test-handler [req]
  (clojure.pprint/pprint req)
  {:status 200 :body "test"})

; CSRF TOKEN

(defn get-csrf-token []
  ; Another option:
  ;(:anti-forgery-token ring-req)] 
  (force *anti-forgery-token*))

#_(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

; WEBSOCKET

(defn ws-token-handler-raw [req]
  (let [token {:csrf-token (get-csrf-token)}]
    (info "csrf token: " token)
    {:status 200
     :body (json/generate-string token)})) ; json must stay! sente has issues if middleware converts this

(defn ws-handshake-handler [req]
  (infof "ws-chsk rcvd: %s" req)
  (let [client-id  (get-in req [:params :client-id]) ; check if sente requirements are met
        uid (get-sente-session-uid req)
        res (ring-ajax-get-or-ws-handshake req)]
    (infof "ws-chsk client-id: %s uid: %s sente res: %s" client-id uid res)
    (info "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    res))

(defn ws-ajax-post-handler [req]
  (infof "/chsk post got: %s" req)
  (let [r (ring-ajax-post req)]
    (info "/chsk post result: " r)
    ;(info "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    r))


(add-ring-handler :ws/token (wrap-webly ws-token-handler-raw))
(add-ring-handler :ws/chsk-get (wrap-webly ws-handshake-handler))
(add-ring-handler :ws/chsk-post (wrap-webly ws-ajax-post-handler))

