(ns goldly.route-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.routes :refer [routes-app routes-api]]))

(def routes-client
  ["/" routes-app])

(defn get-url [url]
  (bidi/match-route routes-client url :request-method :get))

(deftest system-escape-test
  (testing "goldly routes"
    (is (= "/system/6" (bidi/path-for routes-client :goldly/system :system-id 6)))
    (is (= "/system/6/8" (bidi/path-for routes-client :goldly/system-ext :system-id 6 :system-ext 8)))

    (is (= {:route-params {:system-id "6"}
            :handler :goldly/system
            :request-method :get}
           (get-url "/system/6")))

    (is (= {:route-params {:system-id "6" :system-ext "8"}
            :handler :goldly/system-ext,
            :request-method :get}
           (get-url "/system/6/8")))

;
    ))