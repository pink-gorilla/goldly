(ns goldly.service-update-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [goldly.service.result :as sr]))

(def a1 (atom {:a {:b 7}}))
(def a2 (atom {:a {:b 7 :c 2}}))

(deftest services-test
  (testing "service-update-result test"
    (sr/update-atom-where a1 [:a :b] 13)
    (sr/update-atom-where a2 [:a :b] 13)
    (is (= @a1 {:a {:b 13}}))
    (is (= @a2 {:a {:b 13 :c 2}}))
    ;
    ))