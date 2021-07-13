(ns goldly.cljs.loader
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [goldly.service.core :as s]
   [goldly.file.explore :refer [explore-dir load-file!]]
   [goldly.file.watch :refer [watch]]))

(def cljs-dir "goldly/cljs")
(defn cljs-explore []
  (explore-dir cljs-dir))

(defn cljs-watch []
  (watch cljs-dir :goldly/cljs-sci-reload))

(defn cljs-load [filename]
  (load-file! cljs-dir filename))

(s/add {:cljs/explore cljs-explore
        :cljs/load cljs-load})

