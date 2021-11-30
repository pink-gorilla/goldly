(ns goldly.extension.clj
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ;[pinkgorilla.repl :refer [add-require]]
   [modular.config :refer [require-namespaces]]))

(defn add-extension-autoload-clj-ns [{:keys [name
                                             autoload-clj-ns]
                                      :or {autoload-clj-ns []}
                                      :as extension}]
  (when-not (empty? autoload-clj-ns)
    (info name "autoload-clj-ns: " autoload-clj-ns)
    (require-namespaces autoload-clj-ns)))

; clj-require helps in making the require definitions smaller.
#_(defn add-extension-clj-require [{:keys [name
                                           clj-require]
                                    :or {clj-require []}
                                    :as extension}]
    (debug "clj require: " clj-require)
    (doall (for [r clj-require]
             (add-require r))))



