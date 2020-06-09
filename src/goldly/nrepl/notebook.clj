(ns goldly.nrepl.notebook
  (:require
   [clojure.string :as str]
   [pinkgorilla.ui.gorilla-renderable :refer [#_render render-renderable-meta]]))

(def notebooks (atom {}))

(defn log [msg]
  (spit "nrepl-notebook.txt"
        (str "\r\n" (pr-str msg))
        :append true))

(defn cut-namespaces-val [val]
  (if (get-in val [:namespace-definitions])
    "ns-defs"
    val))

(defn render-value [value]
  (let [r (str "XX:" value)]; (render-renderable-meta value)]
    r))

(defn on-nrepl-eval [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
  (when (and op 
             (= op "eval") 
             (find resp :value) 
             (not (str/starts-with? code "(in-ns '")) ; vs code does this before evals
             (not (symbol? value)) ; response to in-ns
             (not (str/starts-with? code "(with-in-str ")) ;vscode load file to repl
             #_(:as-html msg))
    (let [pinkie (render-value value)]
      (log {:ns ns
            :code code
            :value (cut-namespaces-val value)
            :pinkie pinkie
            :out out})
      pinkie)))



