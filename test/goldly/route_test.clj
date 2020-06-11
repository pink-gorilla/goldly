(ns goldly.route-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.routes :refer [routes-bidi]]))

(defn GET [url]
  (bidi/match-route routes-bidi url :request-method :get))

(defn get-handler [url]
  (-> url GET :handler))
 
(deftest app-routes []
  (is (= #'goldly.web.views/app-handler (get-handler "app")))
  (is (= #'goldly.web.views/app-handler (get-handler "app/")))
  (is (= #'goldly.web.views/app-handler (get-handler "/app")))
  (is (= #'goldly.web.views/app-handler (get-handler "/app/"))))



