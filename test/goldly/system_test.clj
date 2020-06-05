(ns goldly.system-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [goldly.system :refer [def-ui escape-symbols]]))

(def-ui y [:p 67])

(deftest system-escape-test
  (testing "1 geometry, no options"
    (let [s (escape-symbols
             {:html [:p/button {:on-click ?incr} @state "s"]
              :fns {:y y
                    :incr (fn [s] (inc s))}})]
      (is (= "{:html [:p/button {:on-click ?incr} (clojure.core/deref state) \"s\"], :fns {:y [:p 67], :incr (fn [s] (inc s))}}"
             s)))))