(ns ui.goldly.core
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [goldly.sci.bindings :refer [add-cljs-namespace add-cljs-bindings generate-bindings]]
   [pinkgorilla.repl.goldly.core] ; side-effects
   ))

; cljs ui
(add-cljs-namespace [ui.site.goldly.core]) ; web-site templates
(add-cljs-namespace [ui.code.goldly.core]) ; codemirror

