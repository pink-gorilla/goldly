(ns goldly.puppet.loader
  "loads systems from files or classpath"
  (:require
   [clojure.string :as str]
   [clojure.java.io]
   [taoensso.timbre :as log :refer (tracef debugf info infof warnf errorf)]))

(defn- ends-with
  [string ending]
  (if (< (count string) (count ending))
    false
    (let [l (count string)
          le (count ending)]
      (= (subs string (- l le) l) ending))))

(defn- clj-file?
  [file]
  (ends-with (.getName file) ".clj"))

(defn- file-name [file]
  (let [tokens {}
        filename (.getPath file)
        filename-canonical (.getPath (.getCanonicalFile file))]
    ; filename-canonical
    filename))

(defn- file-seq-for-dir
  [file]
  (tree-seq
   (fn [f] (.isDirectory f))
   (fn [f] (.listFiles f))
   file))

(defn- files-in-directory
  "get all pink-gorilla filenames in a directory.
   Works recursively, so sub-directories are included."
  [directory]
  (let [c (count directory)]
    (->> (clojure.java.io/file directory)
         file-seq-for-dir
         (filter clj-file?)
         (map file-name)
         (map #(subs % c))
         (map #(subs % 0 (- (count %) 4))))))

(defn- ns-for-file [f]
  (str "systems." f))

(defn- requires-for-directory [directory]
  (let [files (files-in-directory directory)]
    (map ns-for-file files)))

(defn- component-symbols [directory]
  (let [namespaces (requires-for-directory directory)
        symbols (map symbol namespaces)]
    symbols))

(defn load-components-namespaces [namespace-symbols]
  (doall (for [s namespace-symbols]
           (do (info "loading goldly system:" s)
               (require  s))))
  namespace-symbols)

(defn require-components [directory]
  (let [namespaces (requires-for-directory directory)
        symbols (map symbol namespaces)]
    (load-components-namespaces symbols)))

(comment
  (files-in-directory "./src/systems/")
  (requires-for-directory "./src/systems/")
  (component-symbols "./src/systems/")
  (require 'systems.components 'clojure.string)
  (apply require (component-symbols "./src/systems/"))

;  (macroexpand '
  (require-components "./src/systems/")
  ;)
  )