(ns goldly.sci.bindings-static
  (:require
   [clojure.walk :as walk]))

#_(def bindings-static
    {'println println})

(def ns-static
  {'walk {'postwalk walk/postwalk
          'prewalk walk/prewalk
          'keywordize-keys walk/keywordize-keys
          'walk walk/walk
          'postwalk-replace walk/postwalk-replace
          'prewalk-replace walk/prewalk-replace
          'stringify-keys walk/stringify-keys}})