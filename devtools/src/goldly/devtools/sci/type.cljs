(ns goldly.devtools.sci.type
  "converts sci values to hiccup representation"
  (:require
   [sci.lang]
   [reval.type.protocol :refer [hiccup-convertable]]
   [reval.type.ui.simplevalue :refer [simplevalue->hiccup]]
   [reval.type.ui.list :refer [list->hiccup map->hiccup]]))

; this cljs file that gets executed in cljs (NOT BY SCI)
; it needs to be in devtools, because:
; reval does not have sci dependency
; goldly only has sci dependency, but not reval

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

