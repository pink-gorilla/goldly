(ns webly
  (:require
   [clojure.edn :as edn]
   [babashka.tasks :refer [shell]]
   [babashka.fs :as fs]
   ;[fipp.clojure]
   ))



(defn clean-project [dir]
  (println "cleaning dir: " dir)
  (fs/delete-if-exists (str dir "/package.json"))
  (fs/delete-if-exists (str dir "/package-lock.json"))
  (fs/delete-if-exists (str dir "/karma.conf.js"))
  (fs/delete-if-exists (str dir "/shadow-cljs.edn"))

  (fs/delete-tree (str dir "/.cpcache"))
  (fs/delete-tree (str dir "/target"))
  (fs/delete-tree (str dir "/.shadow-cljs"))
  (fs/delete-tree (str dir "/.webly"))
  (fs/delete-tree (str dir "/node_modules"))
  (fs/delete-tree (str dir "/target")))



(defn run [dir alias profile]
  (println "RUNNING WEBLY ALIAS:" alias "PROFILE: " profile  "in dir: " dir)
  (shell {:dir dir} "clojure" (str "-X:" alias) ":profile" profile))


(defn clojure [dir & args]
  (apply shell {:dir dir} "clojure" args))

; clojure -X:webly :profile '"npm-install"'