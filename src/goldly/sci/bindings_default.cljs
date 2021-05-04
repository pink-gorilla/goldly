(ns goldly.sci.bindings-default
  (:require
   [clojure.walk :as walk]))

(defn sin [x]
  (.sin js/Math x))

(def bindings-default
  {'sin sin
   'println println})

(def ^:private walk-ns {'postwalk walk/postwalk
                        'prewalk walk/prewalk
                        'keywordize-keys walk/keywordize-keys
                        'walk walk/walk
                        'postwalk-replace walk/postwalk-replace
                        'prewalk-replace walk/prewalk-replace
                        'stringify-keys walk/stringify-keys})

(def ns-default
  {'walk walk-ns})