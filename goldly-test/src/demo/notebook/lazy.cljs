(ns demo.notebook.lazy
  (:require
   [r]
   [string]
   [user :refer [compile-sci-async println]]))

; this is a sci-cljs notebook!

(defn wrap-code [form]
  (string/join
   "\n"
   (map pr-str form)))

(defn show-atom [a]
  [:p (pr-str @a)])

(defn async-eval [form]
  (let [d (r/atom "async compiling..")
        p (compile-sci-async (wrap-code form))]
    (.then (:result p)
           (fn [result]
             (.log js/console "async result: " result)
             (reset! d result)))
    ^:R
    [show-atom d]))

^:R
[:div
 (async-eval
  '[(ns example1 (:require ["some_js_lib" :as my-lib]))
    (my-lib/libfn)])
 (async-eval
  '[(ns example2 (:require [adder :as x]))
    (x/add 7 13)])
 (async-eval
  '[(ns example3 (:require [funny :as f]))
    (f/joke)])
 #_(async-eval
    '[(ns example4 (:require [funny :as funny]))
      (funny/joke)])
 (async-eval
  '[(ns example5 (:require [funny]))
    (funny/joke)])]




