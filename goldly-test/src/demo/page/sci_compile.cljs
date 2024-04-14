(ns demo.page.sci-compile
  (:require
   [clojure.string :refer [join]]
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.sci :refer [require-async compile-code compile-code-async requiring-resolve resolve-symbol]]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

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
     (let [f (resolve-symbol 'demo.funny/joke)]
       (when f
         [:p "joke: " (f)])))])

(defn sci-compile-page1 [_route-data]
  (let [; sci-require
        r-a (r/atom nil)
        r-p (-> (require-async '[demo.funny :refer [joke]])
                (.then (fn [d]
                         (println "require result received!")
                         (reset! r-a {:data d})))
                (.catch (fn [d]
                          (println "require result failure!")
                          (reset! r-a {:err d}))))
        ; async-sci-compile
        ca-a (r/atom "no async result")
        ca-p (compile-code-async code)
        ; sci-requiring-resolve
        rr-a (r/atom "no-favorite-animal")
        rr-p (requiring-resolve 'demo.dynamic.animal/favorite-animal)]

    (p/then ca-p (fn [result]
                   (reset! ca-a result)))
    (p/catch ca-p (fn [result]
                    (reset! ca-a {:error result})))
    (p/then rr-p (fn [fun]
                   (let [animal (fun)]
                     (reset! rr-a animal))))
    (fn [_route-data]
      [:div.bg-red-200.w-screen.h-screen.p-5
       ; sci-require
       [:p.text-blue-500.text-xxl "sci require  test."]
       [:div "test for require: (require result should show up): " [show-result r-a]]
       ; (sync) sci-compile
       [:p.text-blue-500.text-xxl "sci compile test."]
       [:p "test for compilation: (+ 5 5)" (pr-str (compile-code "(+ 5 5)"))]
       ; async-sci-compile
       [:p.text-blue-500.text-xxl "sci async-compile test."]
       [:p "test for async compilation: " (pr-str @ca-a)]
       [:p.bg-red-500
        [:a {:on-click #(compile-code-async "(println \"hello from sci\")")}
         "click to 'println' to browser console - should say [hello from sci]"]]
       ; sci-requiring-resolve
       [:p.text-blue-500.text-xxl "sci requiring-resolve test"]
       [:p.bg-green-500
        "favorite animal (no-favorite-animal is an error): " @rr-a]])))

(def sci-compile-page
  (wrap-layout sci-compile-page1))

