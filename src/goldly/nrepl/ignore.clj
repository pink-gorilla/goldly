(ns goldly.nrepl.ignore)

(def disabled-ops #{"debug-instrumented-defs"
                    "info"
                    "ns-list"
                    "complete"})

(def enabled-ops
  #{"stacktrace"
    "eval"})

(defn ignore? [{:keys [op] :as req}
               {:keys [completions] :as res}]
  (or (contains? disabled-ops op)
      ;(not (contains? enabled-ops op))
      completions
             ;(nil? ns-list)
      ))

