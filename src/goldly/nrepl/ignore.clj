(ns goldly.nrepl.ignore)

(def disabled-ops #{"debug-instrumented-defs"
                    "info"
                    "ns-list"
                    "complete"})

(def enabled-ops
  #{"stacktrace"
   "eval"})

(defn ignore? [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
  (or ;(contains? disabled-ops op)
      (not (contains? enabled-ops op))
      completions
             ;(nil? ns-list)
      ))

