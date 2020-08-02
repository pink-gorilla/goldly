(ns goldly.route-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.routes :refer [goldly-routes-backend]]))

(defn GET [url]
  (bidi/match-route goldly-routes-backend url :request-method :get))

(defn POST [url]
  (bidi/match-route goldly-routes-backend url :request-method :post))

(defn handler [OP url]
  (-> url OP :handler))

(deftest app-routes []
  ;(is (= #'goldly.web.handler/app-handler (handler GET "app")))
  ;(is (= #'goldly.web.handler/app-handler (handler GET "app/")))
  (is (= :ui/system-list (handler GET "/")))
  #_(is (= #'goldly.web.handler/app-handler (handler GET "/app/"))))

;(get-handler "/app/system/15")
;

(deftest app-routes-greedy []
  (is (= :ui/system (handler GET "/system/15"))))

; websocket

(deftest ws-routes []
  (is (= :ws/token (handler GET  "/api/token")))
  (is (= :ws/chsk-get (handler GET  "/api/chsk")))
  (is (= :ws/chsk-post (handler POST "/api/chsk"))))

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

