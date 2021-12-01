 (ns goldly.static
   (:require
    [taoensso.timbre :refer [debug info infof error]]
    [clojure.java.io :as io]
    [modular.resource.load :refer [write-resources-to]]
    [modular.config :refer [config-atom]]
    [goldly.cljs.discover :refer [autoload-cljs-dir]]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

 ; :autoload-cljs-dir ["goldly/lib"
 ;                    "goldly/devtools"]

(defn- export-path [p]
  (let [to (str "target/sci/" p)]
    (info "writing sci cljs resources: " p)
    (write-resources-to to p)))

(defn export-sci-cljs []
  (ensure-directory "target")
  (ensure-directory "target/sci")
  (doall
   (map export-path (autoload-cljs-dir))))


