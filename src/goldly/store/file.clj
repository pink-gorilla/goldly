(ns goldly.store.file
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [goldly.service.core :as s]
   [goldly.version :refer [load-version]]
   [goldly.extension.core :refer [extension-summary extensions]]
   [goldly.extension.theme :refer [ext-theme]]
   ))

(defn edn-load [filename]
  (let [content (slurp filename)
        data (edn/read-string content)]
    {:filename filename
     :data data}))

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
        :extension/all extensions
        :extension/summary extension-summary
        :extension/theme ext-theme
        :status/sci status-sci
        :edn/load edn-load
        :cljs/explore cljs-explore
        :cljs/load cljs-load
        :cljs/extension load-extension})


