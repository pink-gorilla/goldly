(ns page.sci-compile
  (:require
   [clojure.string :refer [join]]
   [reagent.core :as r]
   [goldly.sci :refer [compile-sci compile-sci-async]]
   [layout]
   [cljs-libs.helper :refer [add-page-test]]))

(def code (join
           "\n"
           (map pr-str '[(ns example (:require ["some_js_lib" :as my-lib]))
                         (my-lib/libfn)])))

(defn sci-compile-page [_route-data]
  (let [async-result (r/atom "no async result")
        p (compile-sci-async code)]
    (.then p (fn [result]
               (reset! async-result result)))
    (fn [_route-data]
      [:div.bg-red-200.w-screen.h-screen.p-5
       [:p.text-blue-500.text-xxl "sci compile test."]
       [:p "test for compilation: " (pr-str (compile-sci "(+ 5 5)"))]
       [:p "test for async compilation: " (pr-str @async-result)]
       [:p.bg-red-500
        [:a {:on-click #(compile-sci-async "(println \"hello from sci\")")}
         "println test"]]])))

(add-page-test sci-compile-page :user/scicompile)