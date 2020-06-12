(ns goldly.handler-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.routes :refer [goldly-handler]]))

(defn GET [url]
  (goldly-handler (mock-request :get url)))

(defn content-type [res]
  (get-in res [:headers "Content-Type"]))

; resources

; app is now greedy.
#_(deftest resource-not-existing []
    (is (= nil
           (-> "alice/in/wonderland/x.ico" GET))))

; resources added by goldly 
(deftest resource-favicon []
  (is (= "image/x-icon"
         (-> "/r/favicon.ico" GET content-type))))

; resources from gorilla-ui

(deftest resource-tailwind []
  (is (= "text/css"
         (-> "/r/tailwindcss/dist/tailwind.css" GET content-type))))

(deftest resource-aggrid []
  (is (= "text/css"
         (-> "/r/ag-grid-community/dist/ag-grid.css" GET content-type)))
  (is (= "text/css"
         (-> "/r/ag-grid-community/dist/ag-theme-balham.css" GET content-type))))

; cljs-bundle

(-> "/r/main.js" GET)

(deftest cljs-bundle-main []
  (is (= "text/javascript"
         (-> "/r/main.js" GET content-type))))

(deftest cljs-bundle-runtime []
  (is (= "text/javascript"
         (-> "/r/cljs-runtime/cljs.core.js" GET content-type)))
  (is (= "application/octet-stream"
         (-> "/r/cljs-runtime/cljs.core.js.map" GET content-type))))

; application

; this actually tests the hiccup conversion
; routing is tested in test.goldly/route-test

(deftest app-html []
  (is (= "text/html; charset=utf-8"
         (-> "/app" GET content-type))))

; websocket

#_(deftest ws-token []
    (is (= "text/html"
           (-> "/token" GET content-type))))

;(-> "/app" GET)

#_(with-redefs [ring.middleware.anti-forgery/*anti-forgery-token* {:csfr-token "15"}]
    ring.middleware.anti-forgery/*anti-forgery-token*
  ;(-> "/token" GET)
    )

;(-> "/chsk" GET)


