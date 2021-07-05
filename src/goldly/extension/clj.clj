(ns goldly.extension.clj
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [pinkgorilla.repl :refer [add-require]]))

(defn add-extension-clj [{:keys [name
                                 clj-require]
                          :or {clj-require []}
                          :as extension}]
  (debug "clj require: " clj-require)
  (doall (for [r clj-require]
           (add-require r))))