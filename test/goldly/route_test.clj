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
  ;(is (= #'goldly.web.handler/app-handler (handler GET "app")))
  ;(is (= #'goldly.web.handler/app-handler (handler GET "app/")))
  (is (= #'goldly.web.handler/app-handler (handler GET "/app")))
  #_(is (= #'goldly.web.handler/app-handler (handler GET "/app/"))))

;(get-handler "/app/system/15")
;

(deftest app-routes-greedy []
  (is (= #'goldly.web.handler/app-handler (handler GET "/system/15"))))

; websocket

(deftest ws-routes []
  (is (= #'goldly.web.handler/ws-token-handler (handler GET  "/token")))
  (is (= #'goldly.web.handler/ws-chsk-get (handler GET  "/chsk")))
  (is (= #'goldly.web.handler/ws-chsk-post (handler POST "/chsk"))))

(comment
(handler GET  "aa")
  (GET "aa")
  (GET "/r/favicon.ico")
  (handler GET  "/r/favicon.ico")
  (handler GET  "favicon.ico")
  (handler GET  "//favicon.ico")

  (handler GET "/tailwindcss/dist/tailwind.css")
  (handler GET  "/app")
  (handler GET  "/system/42")
  (handler GET  "/token")
  (handler GET  "/chsk")

 ; 
  )

