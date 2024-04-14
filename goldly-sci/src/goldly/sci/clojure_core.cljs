(ns goldly.sci.clojure-core
  (:refer-clojure :exclude [println time])
  (:require
   [clojure.string :as str]
   [cljs.core]
   [sci.core :as sci]))

(defn println [& strs]
  (.error js/console (str/join " " strs)))

(clojure.core/defmacro time
  "Evaluates expr and prints the time it took. Returns the value of expr."
  [expr]
  `(let [start# (cljs.core/system-time)
         ret# ~expr]
     (prn (cljs.core/str "Elapsed time: "
                         (.toFixed (- (system-time) start#) 6)
                         " msecs"))
     ret#))

(def cljns (sci/create-ns 'clojure.core nil))