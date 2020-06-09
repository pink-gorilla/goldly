(ns snippets.demo
  (:require
   [pinkie.converter :refer [R]]
   [goldy.nrepl.client :refer [start!]]))

(comment

  ; connect to nrepl of ide
  (start!)
  
  (+ 7 17)
  (println "hello, world!")
  (do (println "a") 2)
  {:a 1}
  ^:R [:p (+ 8 8)]
  ^:R [:p/vega (+ 8 8)]

  (R [:p/vega (+ 8 8)])


; 
  )