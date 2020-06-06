(ns leiningen.tools)

(defn add-dependencies
  "Adds dependencies to the end of the current vector."
  {:added "0.3.3"}
  [project & deps]
  (update-in project [:dependencies] concat deps))