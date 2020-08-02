(ns goldly.handler-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.app :refer [handler]]))

(defn GET [url]
  (handler (mock-request :get url)))

(defn content-type [res]
  (get-in res [:headers "Content-Type"]))

; resources

; app is now greedy.
(deftest resource-not-existing []
  (is (= nil
         (-> "alice/in/wonderland/x.ico" GET))))

; resources added by goldly 
(deftest resource-favicon []
  (is (= "image/x-icon"
         (-> "/r/webly/favicon.ico" GET content-type))))

; resources from gorilla-ui

(deftest resource-tailwind []
  (is (= "text/css"
         (-> "/r/tailwindcss/dist/tailwind.css" GET content-type))))

; application

; this actually tests the hiccup conversion
; routing is tested in test.goldly/route-test

(deftest app-html []
  (is (= "text/html; charset=utf-8"
         (-> "/" GET content-type))))

; websocket

#_(deftest ws-token []
    (is (= "text/html"
           (-> "/api/token" GET content-type))))

;(-> "/app" GET)

#_(with-redefs [ring.middleware.anti-forgery/*anti-forgery-token* {:csfr-token "15"}]
    ring.middleware.anti-forgery/*anti-forgery-token*
  ;(-> "/token" GET)
    )

;(-> "/chsk" GET)


