(ns goldly.sci.bindings-goldly)

; why is this here?

; this is a clojurescript namespace
; this functions have to go to the bundel
; goldly has a system to add code to bundels, and this needs to be tested.


(defn sin [x]
  (.sin js/Math x))