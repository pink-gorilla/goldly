(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(system-start!
 (goldly/system
  {:id :binding-test
   :hidden true
   :state {}
   :html  [:div
           [:p "this shows if cljs bindings are working"]
           [:p.bg-blue-300.mg-3 "sin: " (?mysin 5)]]
   :fns {;:sin-5 (sin 5)
         }
   :fns-raw {:mysin (fn [x] 
                      (info "calculating sin for " x)
                      (sin x)
                      )
             :bongo (fn [u]
                      (+ 78 8)
                      )
             :trott (fn []
                      ;(println "hi")
                      88)
             
             }
   }))