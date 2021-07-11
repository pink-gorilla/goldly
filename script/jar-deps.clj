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
      (str/split #"\n"))
  )


(defn artefact-root [a]
   (str home "/.m2/repository/org/pinkgorilla/" a))

(defn artefact-jar [a v]
  (str home "/.m2/repository/org/pinkgorilla/" a "/" v "/" a "-" v ".jar"))

(defn inspect-artefact [a v]
  ;fastjar tf ~/.m2/repository/org/pinkgorilla/webly/0.3.44/webly-0.3.44.jar
  (let [f (artefact-jar a v)]
    (-> (shell/sh "fastjar" "tf" f)
        :out
        (str/split  #"\n")
    )))

(defn js [a v]
  (let [js-files (->> (inspect-artefact a v)
                      (filter #(str/ends-with? % ".js")))]
    
     (println a v "js-files: " js-files)

    )
  
  
 )


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
   sort
   last
    ;pr-str
    ;println
  ))

(defn release-print [a]
  (println a ": " (release a))
  )

(defn release-print-js [a]
  (let [v (release a)]
    (println a ": " (js a v) )))

;(shell/sh "ls" ".")

;(inspect-artefact "webly" "0.3.44")

;(inspect-artefact "goldly-bundel" "0.3.19")

;(js "goldly-bundel" "0.3.19")

;(lsa "webly")
(release "goldly")
(def alist ["webly" 
            "goldly" "goldly-bundel"
            "notebook" "notebook-bundel"
            "picasso" "pinkie"
            "nrepl-middleware" "gorilla-explore" "notebook-encoding"
            "ui-code" "ui-repl" "ui-markdown" "ui-site" "ui-highlightjs"
            "ui-input" "ui-binaryclock" "ui-cytoscape" "ui-leaflet"
            "ui-highcharts" "ui-aggrid" "ui-math"
            "ui-vega" "ui-gorilla"])


(map release-print alist)

(map release-print-js alist)

