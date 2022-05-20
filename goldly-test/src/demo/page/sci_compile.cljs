
(def code (string/join
           "\n"
           (map pr-str '[(ns example (:require ["some_js_lib" :as my-lib]))
                         (my-lib/libfn)])))

(defn sci-compile-page [route-data]
  (let [async-result (r/atom "no async result")
        p (compile-sci-async code)]
    (println "async promise: " p)
    (.then p (fn [result]
                 (reset! async-result result)))
    (fn [route-data]
      [:div.bg-red-200.w-screen.h-screen.p-5
       [:p.text-blue-500.text-xxl "sci compile test."]
       [:p "test for compilation: " (pr-str (compile-sci "(+ 5 5)"))]
       [:p "test for async compilation: " (pr-str @async-result)]])))

(add-page-test sci-compile-page :user/scicompile)