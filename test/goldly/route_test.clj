(ns goldly.route-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.routes :refer [routes-bidi]]))

(defn GET [url]
  (bidi/match-route routes-bidi url :request-method :get))

(defn POST [url]
  (bidi/match-route routes-bidi url :request-method :post))

(defn handler [OP url]
  (-> url OP :handler))

(deftest app-routes []
  (is (= #'goldly.web.views/app-handler (handler GET "app")))
  (is (= #'goldly.web.views/app-handler (handler GET "app/")))
  (is (= #'goldly.web.views/app-handler (handler GET "/app")))
  (is (= #'goldly.web.views/app-handler (handler GET "/app/"))))

;(get-handler "/app/system/15")

(deftest app-routes-greedy []
  (is (= #'goldly.web.views/app-handler (handler GET "app/system/15"))))

; websocket

(deftest ws-routes []
  (is (= #'goldly.web.handler/token-handler (handler GET  "/token")))
  (is (= #'goldly.web.handler/ws-chsk-get (handler GET  "/chsk")))
  (is (= #'goldly.web.handler/ws-chsk-post (handler POST "/chsk"))))

(handler GET  "/token")

(handler GET  "/app")


