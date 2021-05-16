(ns goldly.sci.bindings-static
  (:require
   [clojure.walk :as walk]
   [goldly.sci.bindings-goldly :refer [sin]]))

(def bindings-static
  {'sin sin
   'println println})

(def ^:private walk-ns {'postwalk walk/postwalk
                        'prewalk walk/prewalk
                        'keywordize-keys walk/keywordize-keys
                        'walk walk/walk
                        'postwalk-replace walk/postwalk-replace
                        'prewalk-replace walk/prewalk-replace
                        'stringify-keys walk/stringify-keys})

(def ns-static
  {'walk walk-ns})