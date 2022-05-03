(ns goldly.version
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [taoensso.timbre :refer [info warn]]))

(defn load-version [app]
  (if-let [r (io/resource (str "META-INF/pink-gorilla/" app "/meta.edn"))]
    (let [data (-> (slurp r) (edn/read-string))]
      data)
    {:app app :error "no version information"}))

(defn print-version [app]
  (let [data (load-version app)]
    (if (:error data)
      (warn app " version unknown!")
      (info app " " (:version data) " generated: " (:generated data)))))

