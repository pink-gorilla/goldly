(ns demo.page.sci-compile
  (:require
   [clojure.string :refer [join]]
   [reagent.core :as r]
   [goldly.sci :refer [require-async compile-sci compile-sci-async]]
   [layout]
   [demo.cljs-libs.helper :refer [add-page-test]]))

(def code (join
           "\n"
           (map pr-str '[(ns example (:require ["some_js_lib" :as my-lib]))
                         (my-lib/libfn)])))

(defn show-result [a]
  [:div
   (if (nil? @a)
     [:p "require pending"]
     [:p "require result: " (pr-str @a)])
   (when (not (nil? @a))
     (let [f (resolve 'demo.funny/joke)]
       (when f
         [:p "joke: " (f)])))])

(defn sci-compile-page [_route-data]
  (let [require-a (r/atom nil)
        require-p (-> (require-async '[demo.funny :refer [joke]])
                      (.then (fn [d]
                               (println "require result received!")
                               (reset! require-a {:data d})))
                      (.catch (fn [d]
                                (println "require result failure!")
                                (reset! require-a {:err d}))))
        async-result (r/atom "no async result")
        p (compile-sci-async code)]
    (.then p (fn [result]
               (reset! async-result result)))
    (fn [_route-data]
      [:div.bg-red-200.w-screen.h-screen.p-5
       [:p.text-blue-500.text-xxl "sci require  test."]
       [:div "test for require: (require result should show up): " [show-result require-a]]
       [:p.text-blue-500.text-xxl "sci compile test."]
       [:p "test for compilation: " (pr-str (compile-sci "(+ 5 5)"))]
       [:p "test for async compilation: " (pr-str @async-result)]
       [:p.bg-red-500
        [:a {:on-click #(compile-sci-async "(println \"hello from sci\")")}
         "click to 'println' to browser console"]]])))

(add-page-test sci-compile-page :user/scicompile)
