(ns goldly.nrepl.ignore)

(def disabled-ops #{"debug-instrumented-defs"
                    "info"
                    "ns-list"
                    "complete"})

(defn ignore? [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
  (or (contains? disabled-ops op)
      completions
             ;(nil? ns-list)
      ))

