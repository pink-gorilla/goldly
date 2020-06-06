(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.string :as str]
   [clojure.java.io]
      ;[cemerick.pomegranate :as pg]
   [goldly.runner :refer [systems-response system-start!]]
   [goldly.web :as web])
  (:gen-class))

#_(defn add-dependencies
    "Use Pomegranate to add dependencies 
   with Maven Central and Clojars as default repositories.
   Same Syntax as clojupyter
   stolen from: https://github.com/clojupyter/clojupyter/blob/40c6d47ec9c9e4634c8e28fca3209b5c3ac8430c/src/clojupyter/misc/helper.clj

   "
    [dependencies & {:keys [repositories]
                     :or {repositories {"central" "https://repo1.maven.org/maven2/"
                                        "clojars" "https://clojars.org/repo"}}}]
    (let [first-item (first dependencies)]
      (if (vector? first-item)
      ; [ [dep1] [dep2]]
        (pg/add-dependencies :coordinates `~dependencies
                             :repositories repositories)
      ; [dep1]
        (pg/add-dependencies :coordinates `[~dependencies]
                             :repositories repositories))))

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

(defn file-name [file]
  (let [tokens {}
        filename (.getPath file)
        filename-canonical (.getPath (.getCanonicalFile file))]
    ; filename-canonical
    filename))

(defn file-seq-for-dir
  [file]
  (tree-seq
   (fn [f] (.isDirectory f))
   (fn [f] (.listFiles f))
   file))

(defn files-in-directory
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

(defn ns-for-file [f]
  (str "systems." f))

(defn requires-for-directory [directory]
  (let [files (files-in-directory directory)]
    (map ns-for-file files)))

(defn component-symbols [directory]
  (let [namespaces (requires-for-directory directory)
        symbols (map symbol namespaces)]
    symbols))

(defn require-components [directory]
  (let [namespaces (requires-for-directory directory)
        symbols (map symbol namespaces)]
    (doall (for [s symbols]
             (do (println "loading goldly system:" s)
                 (require  s))))
    symbols))

(defn goldly-run! [{:keys [port
                           app-systems-dir
                           user-systems-dir]
                    :or {port 8000
                         app-systems-dir "./src/systems/"}}]
  ;(system-start! components)
  (when app-systems-dir
    (println "loading app systems from: " app-systems-dir)
    (require-components app-systems-dir))
  (when user-systems-dir
    (println "loading user systems from: " user-systems-dir)
    (require-components user-systems-dir))
  (web/server-start! {:port port}))

(defn -main [& args]
  (println "goldly app starting with cli-args: " args)
  (let [user-systems-dir (first args)]
    (goldly-run! {:user-systems-dir user-systems-dir}))
  (println "goldly started successfully. systems running: " (systems-response)))

(comment
  (files-in-directory "./src/systems/")
  (requires-for-directory "./src/systems/")
  (component-symbols "./src/systems/")
  (require 'systems.components 'clojure.string)

  (apply require (component-symbols "./src/systems/"))

  (-main)
;  (macroexpand '
  (require-components "./src/systems/")
  ;)
    ;; => (#object[java.io.File 0x12d1f8bf "./src/goldly/systems/components.clj"])
  )