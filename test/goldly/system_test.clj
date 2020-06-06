(ns goldly.system-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [goldly.system :refer [def-ui escape-html]]))

(def-ui y [:p 67])

(deftest system-escape-test
  (testing "html symbol escaping"
    (let [s (escape-html
             {:html [:p/button {:on-click ?incr} @state "s"]
              :fns {:y y
                    :incr (fn [s] (inc s))}})]
      (is (= "{:html [:p/button {:on-click ?incr} (clojure.core/deref state) \"s\"], :fns {:y [:p 67], :incr (fn [s] (inc s))}}"
             s)))))