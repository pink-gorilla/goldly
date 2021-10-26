#!/usr/bin/env bb

(ns jarer.core
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [clojure.tools.cli :as tools.cli]
            ;[babashka.fs :as fs]
            ))
(def home (System/getenv "HOME"))

(defn ls [dir]
  (-> (shell/sh "ls" dir)
      :out
      (str/split #"\n")))


(defn artefact-root [a]
   (str home "/.m2/repository/org/pinkgorilla/" a))

(defn artefact-jar [a v]
  (str home "/.m2/repository/org/pinkgorilla/" a "/" v "/" a "-" v ".jar"))

(defn artefact-res [a v]
  ;fastjar tf ~/.m2/repository/org/pinkgorilla/webly/0.3.44/webly-0.3.44.jar
  (let [f (artefact-jar a v)]
    (-> (shell/sh "fastjar" "tf" f)
        :out
        (str/split  #"\n")
    )))

(defn version [v-str]
  (let [m (re-matches #"([0-9]*)\.([0-9]*)\.([0-9]*).*" v-str)
        [_ major minor patch] m
        major (if major (Integer/parseInt major) 0)
        minor (if minor (Integer/parseInt minor) 0)
        patch (if patch (Integer/parseInt patch) 0)]
    {:major major
     :minor minor
     :patch patch
     :v v-str
     :idx (+ (* major 1000000) (* minor 1000) patch)}))

(version "13.7.9")
(version "bongo")

(defn release [artefact]
  (->>
   (artefact-root artefact)
   (ls)
   ;(fs/list-dir)
    ;(map str/trim)
   (remove #(str/ends-with? % ".xml"))
   (remove #(str/ends-with? % ".sha1"))
   (remove #(str/starts-with? % "maven"))
   (remove #(str/starts-with? % "resolver"))
   (map version)
   (sort-by :idx)
   last
   :v
   ))


(defn ext [e]
  (fn [f]
    (str/ends-with? f e)))


(defn release-print [a]
  (println a ": " (release a)))

(defn release-map [a]
  {:name a
   :version (release a)}
  )

(defn release-print-res 
  ([a]
  (let [v (release a)
        files (artefact-res a v)]
    (println a v files)))
   ([pred a]
    (let [v (release a)
          files (artefact-res a v)
          files (filter pred files)
          ]
      (println a v files)))
  )


(def alist [;webly
            "webly"
            ; clj kernel
            "picasso"
            "pinkie"
            "nrepl-middleware"
            ; goldly core
            "goldly"
            "ui-site" "ui-repl"
            "ui-code" "ui-highlightjs" "ui-markdown"
            ; goldly bundel
            "goldly-bundel"
            "ui-binaryclock" "ui-cytoscape" "ui-leaflet"
            "ui-highcharts" "ui-aggrid"
            "ui-input" "ui-gorilla" "ui-vega"
            ; unused
            "ui-math"
            ; "ui-quil"

            ; notebook
            "notebook" "notebook-bundel"
            "gorilla-explore" "notebook-encoding"
            ])


;(shell/sh "ls" ".")
(release-print "goldly")

;(artefact-res "webly" "0.3.44")
;(artefact-res "goldly-bundel" "0.3.19")
(release-print-res "ui-aggrid")
(release-print-res "ui-binaryclock")

;(js "goldly-bundel" "0.3.19")


(println "\nartefact versions:")
(doall (map release-print alist))
(clojure.pprint/print-table (map release-map alist))

(println "\nartefact js resources:")
(doall (map (partial release-print-res (ext ".js")) alist))

(println "\nartefact cljs resources:")
(doall (map (partial release-print-res (ext ".cljs")) alist))

(println "\nartefact css resources:")
(doall (map (partial release-print-res (ext ".css")) alist))

(println "\nartefact cljs-bindings resources [should be empty!]:")
(doall (map (partial release-print-res (ext "goldly_bindings_generated.cljs")) alist))


(clojure.pprint/print-table [{:a "a"} {:a "b"}])