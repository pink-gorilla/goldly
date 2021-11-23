(ns goldly.service-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [goldly.service.core :as s]))

(defn fun-noargs []
  27)

(defn fun-add [a b]
  (+ a b))

(defn fun-ex []
  (throw (Exception. "bad")))

(s/add {:test/noargs fun-noargs
        :test/add fun-add
        :test/ex fun-ex})

(defn err? [r]
  (and (:error r)
       (= nil (:result r))))

(deftest services-test
  (testing "services test"
    (let [a (s/run :test/noargs)
          b (s/run :test/add 2 7)
          c (s/run :test/ex) ; called fn throws ex
          d (s/run :test/add)  ; missing params
          e (s/run :test/unknown) ; unknown service
          ]
      (is (= a {:result 27}))
      (is (= b {:result 9}))
      (is (err? c))
      (is (err? d))
      (is (err? e)))))