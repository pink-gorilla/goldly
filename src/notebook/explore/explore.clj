(ns notebook.explore.explore
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.java.io :as io]))

(defn dir? [filename]
  (-> (io/file filename) .isDirectory))

(defn explore-dir [dir purpose]
  (let [dir (io/file dir)
        files (if (.exists dir)
                (into [] (->> (.list dir)
                              (remove dir?)
                              doall))
                (do
                  (warnf "path for: %s not found: %s" purpose dir)
                  []))]
    (debug "explore-dir: " files)
    ;(warn "type file:" (type (first files)) "dir?: " (dir? (first files)))
    files))

(defn load-file! [filename]
  (let [code (slurp filename)]
    {:filename filename
     :code code}))
