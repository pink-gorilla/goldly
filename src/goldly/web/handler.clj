(ns goldly.web.handler
  (:require
   [clojure.string]
   [clojure.pprint]
   [taoensso.timbre :as log :refer (tracef debugf info infof warnf errorf)]
   [cheshire.core :as json]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.middleware :refer [wrap-goldly]]
   [goldly.web.views :refer [app-page not-found-page]]
   [goldly.web.ws :refer [get-sente-session-uid sente-session-with-uid
                          ring-ajax-get-or-ws-handshake ring-ajax-post]]))

(defn html-response [html]
  (response/content-type
   {:status 200
    :body html}
   "text/html"))


; CSRF TOKEN


(defn get-csrf-token []
  ; Another option:
  ;(:anti-forgery-token ring-req)] 
  (force *anti-forgery-token*))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

(defn not-found-handler [req]
  (let [;csrf-token (get-csrf-token)
        ;session (if (session-uid req)
        ;          (:session req)
        ;          (assoc (:session req) :uid (unique-id)))
        ]
    (response/content-type
     {:status 404
     ; :session session
      :body (not-found-page)}
     "text/html")))

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

;; APP

(defn app-handler-raw [req]
  (let [; csrf-token and session are sente requirements
        csrf-token (get-csrf-token)
        session  (sente-session-with-uid req)
        res (response/content-type
             {:status 200
              :session session
              :body (app-page csrf-token)}
             "text/html")]
    (response/header res "session" session)))

(def app-handler
  (-> app-handler-raw
      wrap-goldly))

(def ws-token-handler
  (-> ws-token-handler-raw
      wrap-goldly))

(def ws-chsk-get
  (-> ws-handshake-handler
      wrap-goldly))

(def ws-chsk-post
  (-> ws-ajax-post-handler
      wrap-goldly))


