(ns ui.goldly.core
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [goldly.sci.bindings :refer [add-cljs-namespace add-cljs-bindings add-cljs-ns-bindings generate-bindings]]
   ;[pinkgorilla.repl.goldly.core] ; side-effects
   ;[pinkgorilla.repl.goldly.snippets] ; side-effects
   ))
(add-cljs-namespace [clojure.walk])
(add-cljs-ns-bindings
 'walk {'postwalk clojure.walk/postwalk
        'prewalk clojure.walk/prewalk
        'keywordize-keys clojure.walk/keywordize-keys
        'walk clojure.walk/walk
        'postwalk-replace clojure.walk/postwalk-replace
        'prewalk-replace clojure.walk/prewalk-replace
        'stringify-keys clojure.walk/stringify-keys})