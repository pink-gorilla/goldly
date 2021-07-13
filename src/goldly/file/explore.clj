(ns goldly.file.explore
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.java.io :as io]))

(defn explore-dir [dir]
  (let [dir (io/file dir)
        files (if (.exists dir)
                (into [] (.list dir))
                [])]
    (debug "explore-dir: " files)
    files))

(defn load-file! [dir filename]
  (let [code (slurp (str dir "/" filename))]
    {:filename filename
     :code code}))