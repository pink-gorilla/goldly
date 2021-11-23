(ns goldly.extension.clj
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [pinkgorilla.repl :refer [add-require]]
   [goldly.require-clj :refer [require-clj-namespaces]]))

(defn add-extension-clj-require [{:keys [name
                                         clj-require]
                                  :or {clj-require []}
                                  :as extension}]
  (debug "clj require: " clj-require)
  (doall (for [r clj-require]
           (add-require r))))

(defn add-extension-autoload-clj-ns [{:keys [name
                                             autoload-clj-ns]
                                      :or {autoload-clj-ns []}
                                      :as extension}]
  (when-not (empty? autoload-clj-ns)
    (info name "autoload-clj-ns: " autoload-clj-ns)
    (require-clj-namespaces autoload-clj-ns)))


