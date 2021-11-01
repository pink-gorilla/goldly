(ns goldly.sci.sci-types
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf info error]]

   [sci.impl.vars]
   [sci.impl.vars :refer [SciNamespace]]
   ;[picasso.protocols :refer [Renderable render]]
   ;[picasso.render.span :refer [span-render]]
   ))

#_(extend-type sci.impl.vars/SciVar
  Renderable
  (render [self]
    (span-render self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
  Renderable
  (render [self]
    ;(error "sci ns: " (pr-str self))
    (span-render (.toString self) "clj-namespace")))

