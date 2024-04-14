(ns reval.type.sci
  "converts sci values to hiccup representation"
  (:require
   [sci.lang]
   [reval.type.protocol :refer [hiccup-convertable #_to-hiccup]]
   [reval.type.ui.simplevalue :refer [simplevalue->hiccup]]
   [reval.type.ui.list :refer [list->hiccup map->hiccup]]))

; this cljs file that gets executed in cljs (NOT BY SCI)

#_(extend-type sci.impl.vars/SciVar
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-namespace")))

#_(extend-type sci.impl.vars/SciVar
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-namespace")))

(extend-type sci.lang/Var
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-symbol")))

