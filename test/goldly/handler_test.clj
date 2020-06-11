(ns goldly.handler-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.routes :refer [handler]]))

(defn GET [url]
  (handler (mock-request :get url)))

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
         (-> "favicon.ico" GET content-type))))

; resources from gorilla-ui

(deftest resource-tailwind []
  (is (= "text/css"
         (-> "tailwindcss/dist/tailwind.css" GET content-type))))

(deftest resource-aggrid []
  (is (= "text/css"
         (-> "ag-grid-community/dist/ag-grid.css" GET content-type)))
  (is (= "text/css"
         (-> "ag-grid-community/dist/ag-theme-balham.css" GET content-type))))

; cljs-bundle

(-> "main.js" GET)

(deftest cljs-bundle-main []
  (is (= "text/javascript"
         (-> "main.js" GET content-type))))

(deftest cljs-bundle-runtime []
  (is (= "text/javascript"
         (-> "cljs-runtime/cljs.core.js" GET content-type)))
  (is (= "application/octet-stream"
         (-> "cljs-runtime/cljs.core.js.map" GET content-type))))

; application

; this actually tests the hiccup conversion
; routing is tested in test.goldly/route-test

(deftest app-html []
  (is (= "text/html"
         (-> "/app" GET content-type))))



