(ns goldly.build
  (:require
   [clojure.string]
   [clojure.java.io :as io]
   [babashka.fs :refer [create-dirs]]
   [extension :refer [get-extensions-for write-service]]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn ns->filename [ns]
  (-> ns
      (clojure.string/replace #"\." "/")
      (clojure.string/replace #"\-" "_")))

(defn slurp-res [name-full]
  (let [file-content (try
                       (slurp name-full)
                       (catch Exception _
                         nil))]
    (if file-content
      file-content
      (try (let [r (io/resource name-full)]
             (slurp r))
           (catch Exception _
             nil)))))

(defn export-ns [dir ns]
  (let [filename (str (ns->filename ns) ".cljs")
        content (slurp-res filename)
        filename-out (str dir "/" filename)
        idx (clojure.string/last-index-of filename-out "/")
        dir-out (subs filename-out 0 idx)]
    (println "exporting sci file " filename " to: " filename-out "dir-out: " dir-out)
    (if (or (nil? content) (clojure.string/blank? content))
      (println "no resource for: " filename)
      (do (create-dirs dir-out)
          (spit filename-out content)))))

(defn write-files [dir sci-files]
  (doall (map #(export-ns dir %) sci-files)))

(defn export-sci-code [exts out-dir]
  (let [sci-files (->> (get-extensions-for exts :sci-cljs-ns  concat [] [])
                       (into []))]
    (write-service exts :sci-cljs-ns sci-files)
    (ensure-directory out-dir)
    (write-files out-dir sci-files)))

(defn static-build-copy-sci-code [& _]
  (let [exts (extension/discover)]
    (export-sci-code exts "target/static/r/code")))

(comment
  (def exts (extension/discover))
  (export-sci-code exts "target/static/r/code")

;
  )


