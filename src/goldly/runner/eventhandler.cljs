(ns goldly.runner.eventhandler
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

(defn- norm-evt [obj]
  (->> obj
       js/Object.getPrototypeOf
       js/Object.getOwnPropertyNames
       (map #(let [norm (-> %
                            (str/replace #"[A-Z]" (fn [r] (str "-" (str/lower-case r))))
                            keyword)]
               [norm (aget obj %)]))
       (filter (comp edn? second))
       (into {})))

(defn- eventhandler-fn [state fun]
  (fn [e & args]
    (try
      (info "running eventhandler fn: " fun "e:" e " args: " args)
      ;(info "running eventhandler with state: " @state)
      (.preventDefault e)
      (.stopPropagation e)
      (let [e-norm (norm-evt (.-target e))
            _   (info "eventhandler e-norm: " e-norm)
            _ (info "args: " args)
            fun-args [e-norm @state]
            fun-args (if (nil? args)
                       fun-args
                       (into [] (concat fun-args args)))
            _ (info "fun-args: " fun-args)]
        (->> (apply fun fun-args)
             (reset! state))
        (info "new state: " @state))
      (catch :default e
        (.log js/console "eventhandler-fn exception: " e)))))