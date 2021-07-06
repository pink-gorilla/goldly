(ns goldly.store.file
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [goldly.service.core :as s]
   [goldly.version :refer [load-version]]))

(defn edn-load [filename]
  (let [content (slurp filename)
        data (edn/read-string content)]
    {:filename filename
     :data data}))

(defn status-extensions []
  (edn-load ".webly/extensions.edn"))

(defn load-extension [name]
  (let [content (slurp ".webly/extensions.edn")
        extensions (edn/read-string content)
        e (first (filter #(= name (:name %)) extensions))]
    e))
(defn status-sci []
  (edn-load ".webly/sci-cljs-bindings.edn"))

(defn cljs-explore []
  (let [dir (io/file "cljs-sci")
        files (if (.exists dir)
                (into [] (.list dir))
                [])]
    (debug "cljs-sci explore: " files)
    files))

(defn cljs-load [filename]
  (let [code (slurp (str "cljs-sci/" filename))]
    {:filename filename
     :code code}))

(s/add {:goldly/version load-version
        :goldly/services s/services-list
        :status/extensions status-extensions
        :status/sci status-sci
        :edn/load edn-load
        :cljs/explore cljs-explore
        :cljs/load cljs-load
        :cljs/extension load-extension})


