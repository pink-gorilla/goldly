(ns goldly.system.eventhandler
  (:require
   [clojure.string :as str]
   [taoensso.timbre :as timbre :refer-macros [debug debugf info infof error]]))

(defn- edn? [obj]
  (or (number? obj)
      (string? obj)
      (coll? obj)
      (boolean? obj)
      (nil? obj)
      (regexp? obj)
      (symbol? obj)
      (keyword? obj)))

(defn norm-evt [obj]
  (->> obj
       js/Object.getPrototypeOf
       js/Object.getOwnPropertyNames
       (map #(let [norm (-> %
                            (str/replace #"[A-Z]" (fn [r] (str "-" (str/lower-case r))))
                            keyword)]
               [norm (aget obj %)]))
       (filter (comp edn? second))
       (into {})))

(defn eventhandler-fn [state fun]
  (fn [e & args]
    (try
      (info "running eventhandler fn with args: " args) ; fun  - fun displays source
      ;(info "running eventhandler with state: " @state)
      (.preventDefault e)
      (.stopPropagation e)
      (let [e-norm (norm-evt (.-target e))
            _   (debug "eventhandler e-norm: " e-norm "args: " args)
            fun-args [e-norm @state]
            fun-args (if (nil? args)
                       fun-args
                       (into [] (concat fun-args args)))
            _ (debug "fun-args: " fun-args)]
        (->> (apply fun fun-args)
             (reset! state))
        (info "new state: " @state))
      (catch :default e
        (.log js/console "eventhandler-fn exception: " e)))))