(ns snippets.demo
  (:require
   [pinkie.converter :refer [R]]
   [goldly.app :refer [goldly-run!]]
   [pinkgorilla.nrepl.sniffer.core :refer [start-sniffer!]]
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
  ; in VS-Code:
  ; jack-in -> leiningen -> no alias -> demo profile

  ; connect to nrepl session of IDE
  (start-sniffer!)

  ; after executing (start!), all the following evals will be
  ; captured by goldly nrepl middleware
  (+ 7 17)
  (println "hello, world!")
  (do (println "a") 2)
  {:a 1}
  ^:R [:p (+ 8 8)]  ; does not work in vs-code (vs-code does not send meta data)
  ^:R [:p/vega (+ 8 8)]
  (R [:p/vega (+ 8 8)]) ; works in vs-code also

  (def data
    {:$schema "https://vega.github.io/schema/vega-lite/v4.json"
     :description "A simple bar chart with embedded data."
     :data {:values [{:a "A" :b 28} {:a "B" :b 55} {:a "C" :b 43} {:a "D" :b 91} {:a "E" :b 81} {:a "F" :b 53}
                     {:a "G" :b 19} {:a "H" :b 87} {:a "I" :b 52} {:a "J" :b 127}]}
     :mark "bar"
     :encoding {:x {:field "a" :type "ordinal"}
                :y {:field "b" :type "quantitative"}}})

  (R [:p/vega data])

  (R [:p/player "https://www.youtube.com/watch?v=Bs44qdAX5yo"])



  ; test how to add an item to an array in specter


  (def data {:a [1 2 3]})
  (require '[com.rpl.specter])
  (com.rpl.specter/setval [:a com.rpl.specter/END] [4 5] data)
  (namespace 'com.rpl.specter/END)
  (symbol? 'com.rpl.specter/END)
  (resolve 'com.rpl.specter/END)

  ;(require '[clojure.java.classpath :as cp])
  ;(cp/classpath)


; 
  )