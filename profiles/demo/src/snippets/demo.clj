(ns snippets.demo
  (:require
   [pinkie.converter :refer [R]]
   [goldly.app :refer [goldly-run!]]
   [goldly.nrepl.client :refer [start!]]
   [systems.snippets :refer [publish-eval!]]))

(comment

  (goldly-run! {})
    ; send demo eval results to system:
  (publish-eval!  {:id "86"
                   :ns "goldy.nrepl.client"
                   :code "(pinkie.converter/R [:p/vega (+ 8 8)])"
                   :value [:p/vega 16]
                   :pinkie [:p/vega
                            {:data {:values [{:x 3 :y 4} {:x 7 :y 1}]}
                             :mark :point
                             :encoding {:x {:field :x :type :quantitative}
                                        :y {:field :y :type :quantitative}}}]})

  ; this code should be run in your ide;
  ; we tested with vs-code.
  ; In VS-Code:
  ; jack-in -> leiningen -> no alias -> demo profile

  ; connect to nrepl session of IDE
  (start!)

  ; after executing (start!), all the following evals will be
  ; captured by goldly nrepl middleware
  (+ 7 17)
  (println "hello, world!")
  (do (println "a") 2)
  {:a 1}
  ^:R [:p (+ 8 8)]  ; does not work in vs-code (vs-code does not send meta data)
  ^:R [:p/vega (+ 8 8)]
  (R [:p/vega (+ 8 8)]) ; works in vs-code also

  

  ; test how to add an item to an array in specter
  (def data {:a [1 2 3]})
  (require '[com.rpl.specter])
  (com.rpl.specter/setval [:a com.rpl.specter/END] [4 5] data)
  (namespace 'com.rpl.specter/END)
  (symbol? 'com.rpl.specter/END)
  (resolve 'com.rpl.specter/END)

  (require '[clojure.java.classpath :as cp])
  (cp/classpath)


; 
  )