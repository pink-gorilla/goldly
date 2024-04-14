(ns goldly.sci.eventhandler
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

(defn eventhandler-fn [fun]
  (fn [e & args]
    (try
      ;(info "eventhandler-fn args: " args) ; fun  - fun displays source
      ;(info "running eventhandler with state: " @state)
      (.preventDefault e)
      (.stopPropagation e)
      (let [t (.-target e)
            v (.-value t)
            e-norm (norm-evt t)
            _   (info "eventhandler v:" v  " e-norm: " e-norm " args: " args)
            fun-args [v e-norm] ; todo: allow additional properties , not only FUN
            fun-args (if (nil? args)
                       fun-args
                       (into [] (concat fun-args args)))
            _ (debug "fun-args: " fun-args)]
        (apply fun fun-args))
      (catch :default err
        (error "eventhandler-fn exception: " err)))))