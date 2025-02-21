(ns demo.page.sci-compile
  (:require
   [clojure.string :refer [join]]
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.sci :refer [require-async compile-code compile-code-async 
                       requiring-resolve resolve-symbol]]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn compile-sync-test []
  [:div 
    "sync compile result for: (+ 5 5)" (pr-str (compile-code "(+ 5 5)"))])
  

(defn compile-async-test []
  (let [a (r/atom "not clicked yet.")
        code "(+ 7 7)"
        run (fn [& _]
              (println "compile-async-test code: " code)
              (let [rp (compile-code-async code)]
                (-> rp 
                    (p/then (fn [res]
                              (println "compile-code-async result: " res)
                              (reset! a (pr-str res))))
                    (p/catch (fn [err]
                               (println "compile-code-async error: " err)
                               (reset! a "error"))))))]
    (fn []
    [:div 
       [:a {:class "bg-blue-500" :on-click run} [:p.bg-green-500 "click to see add result"]]
       [:div "result: " @a]])))

(defn compile-async-fail-test []
  (let [code (join
              "\n"
              (map pr-str '[(ns example (:require ["some_js_lib" :as my-lib]))
                            (my-lib/libfn)]))
        ca-a (r/atom "no async result")
        ca-p (compile-code-async code)]
    (-> ca-p
        (p/then (fn [result]
                  (println "compile-async result: " result)
                  (reset! ca-a (pr-str result))))
        (p/catch ca-p (fn [err]
                        (println "compile-async error: " err)
                        (reset! ca-a (str "error: " (pr-str err))))))
    (fn []
      [:div "result: " @ca-a])))



(defn require-async-test []
  (let [a (r/atom "pending")
        _ (-> (require-async '[demo.dynamic.funny :refer [joke]])
                (p/then (fn [d]
                         (println "async require success! data: " d)
                         (if-let [f (resolve-symbol 'demo.dynamic.funny/joke)]
                           (do (println "joke is resolved: " (pr-str f))
                               (reset! a (str "joke: " (f))))
                           (reset! a "error: cound not resolve 'demo.funny/joke)"))))
                (p/catch (fn [error]
                         (println "require result failure! error: " error)
                         (reset! a "error: could not require-async demo.funny"))))]
  (fn []
    [:div
      [:p "result: " (pr-str @a)]])))

(defn requiring-resolve-test []
  (let [rr-a (r/atom "no-favorite-animal")
        rr2-a (r/atom "no-favorite-animal")
        rr-p (requiring-resolve 'demo.dynamic.animal/favorite-animal)
        ]
    (->  rr-p
         (p/then  (fn [fun]
                        (println "animal1: " (pr-str fun))
                        (let [animal (fun)
                              rr2-p (requiring-resolve 'demo.dynamic.animal/favorite-animal)]
                          (reset! rr-a animal)
                          (->  rr2-p
                               (p/then  (fn [fun]
                                          (println "animal2: " (pr-str fun))
                                          (let [animal (fun)]
                                            (reset! rr2-a animal))))))))
         (p/catch (fn [err]
                    (println "animal-test error: " err)
                    (reset! rr-a "error"))))
    (fn []
      [:div 
       "result: " @rr-a
       "result2: " @rr2-a
       ])))



(defn sci-compile-page1 [_route-data]
  [:div.bg-red-200.w-screen.h-screen.p-5
        ; (sync) sci-compile
       [:p.text-blue-500.text-xxl "sci compile (sync)"]
       [:div.text-red-500 
        [compile-sync-test]]
   
       ; async-sci-compile
       [:p.text-blue-500.text-xxl "sci compile (async)"]
       [:div.text-red-500
        [compile-async-test]]
   
      ; async-sci-compile
       [:p.text-blue-500.text-xxl "sci compile (async) will fail"]
       [:div.text-red-500
        [compile-async-fail-test]]
   
       ; sci-requiring-resolve
        [:p.text-blue-500.text-xxl "sci requiring-resolve test"]
        [:div.text-red-500
          [requiring-resolve-test]]

       ; sci-require
       [:p.text-blue-500.text-xxl "sci require (async) test."]
       [:div.text-red-500
        [require-async-test]]
           
       
       
      
       
       ])

(def sci-compile-page
  (wrap-layout sci-compile-page1))

