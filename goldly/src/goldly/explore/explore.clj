(ns goldly.explore.explore
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

(defn load-file-or-res! [filename]
  (let [r (io/resource filename)
        code-res  (try (slurp r)
                       (catch Exception _
                         nil))
        code (if code-res
               code-res
               (slurp filename))]
    {:filename filename
     :code code}))

(comment
  (-> (io/resource "goldly/lib/util.cljs")
      slurp)

  (load-file-or-res! "goldly/lib/util.cljs")
  (load-file-or-res! "src/demo/page/info.cljs"))