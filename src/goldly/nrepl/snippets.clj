(ns goldly.nrepl.snippets
  (:require
   [clojure.string :as str]
   [pinkie.converter :refer [->pinkie]]
   [pinkie.clj-types] ; side effects! 
   ))

(def notebooks (atom {}))

(defn log [msg]
  (spit "nrepl-snippets.txt"
        (str "\r\n" (pr-str msg))
        :append true))

(defn cut-namespaces-val [val]
  (if (get-in val [:namespace-definitions])
    "ns-defs"
    val))

(defn render-value [value]
  (let [r (->pinkie value)] ; (str "XX:" value)];
    r))

(defn on-nrepl-eval [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
  (when (and op (= op "pinkieeval"))
    (log {:pinkie-eval "how great is this?"
          :code code}))
  (when (and op
             (= op "eval")
             ;ns
             (or (and ns (find resp :value))
                 (and (nil? ns) out))
             (not (str/starts-with? code "(in-ns '")) ; vs code does this before evals
             (not (symbol? value)) ; response to in-ns
             (not (str/starts-with? code "(with-in-str ")) ;vscode load file to repl
             #_(:as-html msg))
    (let [pinkie (if value (render-value value) nil)]
      (log {:mval (meta value)
            :mcode (meta code)})
      #_(log resp)
      (log {:session session
            :id id
            :ns ns
            :code code
            :value value
            :pinkie pinkie
            :out out})
      pinkie)))



