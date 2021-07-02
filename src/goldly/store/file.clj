(ns goldly.store.file
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [goldly.service.core :as s]))

(defn edn-load [filename]
  (let [content (slurp filename)
        data (edn/read-string content)]
    {:filename filename
     :data data}))

(defn status-extensions []
  (edn-load ".webly/extensions.edn"))
(defn status-sci []
  (edn-load ".webly/sci-cljs-bindings.edn"))

(defn cljs-explore []
  (let [files (.list (io/file "cljs-sci"))
        files (into [] files)]
    (info "cljs-sci explore: " files)
    files))

(defn cljs-load [filename]
  (let [code (slurp (str "cljs-sci/" filename))]
    {:filename filename
     :code code}))

(s/add {:edn/load edn-load
        :status/extensions status-extensions
        :status/sci status-sci
        :cljs/explore cljs-explore
        :cljs/load cljs-load})


