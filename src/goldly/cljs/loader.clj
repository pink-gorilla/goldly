(ns goldly.cljs.loader
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [webly.config :refer [get-in-config]]
   [goldly.service.core :as s]
   [notebook.explore.explore :refer [explore-dir load-file!]]
   [notebook.explore.watch :refer [watch]]))

(defn autoload-dir []
  (get-in-config [:goldly :autoload-dir]))
(defn cljs-explore []
  (let [dir (autoload-dir)]
    (explore-dir dir "autoload-dir")))
(defn cljs-watch []
  (let [dir (autoload-dir)]
    (watch dir :goldly/cljs-sci-reload)))

(defn cljs-load [filename]
  (assert (string? filename))
  (warn "loading cljs file: " filename)
  (let [dir (autoload-dir)]
    (load-file! dir filename)))

(s/add {:cljs/explore cljs-explore
        :cljs/load cljs-load})

