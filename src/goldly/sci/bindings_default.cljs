(ns goldly.sci.bindings-default)

(defn sin [x]
  (.sin js/Math x))

(def bindings-default
  {'sin sin
   'println println})

