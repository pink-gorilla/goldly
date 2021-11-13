(ns goldly.snippet-test
  (:require
   [clojure.core :refer [read-string]]
   [clojure.test :refer [deftest is testing]]
   [goldly.system :refer [escape-html2]]))

(def src (pr-str '{:html [:p/button {:on-click ?incr} @state "s"]
                   :fns {:y [:p 67]
                         :incr (fn [s] (inc s))}}))

(defn src->system [id src]
  (pr-str
   (assoc (read-string src) :id id)))

(println "src:   " src)

#_(def src (str "{:html [:p/button {:on-click ?incr} @state]} \"s\"]"
                ":fns {:y y  "
                ":incr (fn [s] (inc s))}}"))

(println "src-e: " (read-string src))
(println "src-p: " (pr-str (read-string src)))
(println "src-E: " "{:html [:p/button {:on-click ?incr} (clojure.core/deref state) \"s\"], :fns {:y [:p 67], :incr (fn [s] (inc s))}}")

(deftest snippet-escape-test
  (testing "html symbol escaping"
    (let [s ;(escape-html2
          (src->system 77 src)]
      (is (= "{:html [:p/button {:on-click ?incr} (clojure.core/deref state) \"s\"], :fns {:y [:p 67], :incr (fn [s] (inc s))}, :id 77}"
             s)))))