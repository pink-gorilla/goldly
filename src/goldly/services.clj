(ns goldly.services
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [goldly.service.core :as s]
   [goldly.version :refer [load-version]]
   [goldly.extension.core :refer [extension-summary extensions]]
   [goldly.extension.theme :refer [ext-theme]]))

(defn edn-load [filename]
  (let [content (slurp filename)
        data (edn/read-string content)]
    {:filename filename
     :data data}))

(defn edn-load-res [filename]
  (if-let [r (io/resource filename)]
    (let [content (slurp r)
          data (edn/read-string content)]
      {:filename filename
       :data data})
    {:error (str "resource not found: " filename)}))

(defn load-extension [name]
  (let [content (slurp ".webly/extensions.edn")
        extensions (edn/read-string content)
        e (first (filter #(= name (:name %)) extensions))]
    e))
(defn status-sci []
  (edn-load-res "public/sci-cljs-bindings.edn"))

(s/add {:goldly/version load-version
        :goldly/services s/services-list

        :extension/all extensions
        :extension/summary extension-summary
        :extension/theme ext-theme
        :extension/load load-extension

        :status/sci status-sci
        :edn/load edn-load})


