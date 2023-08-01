(ns goldly.run.services
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [goldly.service.core :as s]
   [goldly.service.handler] ; side effects
   [goldly.version :refer [load-version]]))

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

(defn get-extension-info [name]
  (let [content (slurp ".webly/extensions.edn")
        extensions (edn/read-string content)
        e (first (filter #(= name (:name %)) extensions))]
    e))

(defn build-sci-config []
  (edn-load-res "public/goldly-build-sci-config.edn"))

(defn run-sci-cljs-autoload []
  (edn-load-res "public/goldly-run-sci-cljs-autoload.edn"))

(defn sci-bindings []
  (edn-load-res "public/goldly-build-sci-config.edn"))

(defn goldly-version []
  (load-version "goldly"))

(comment
    ;compile time
  (load-version "goldly")
  ;(extension-summary)
  ;(extensions)
  (sci-bindings)

  (get-extension-info "ui-code")
  ;(ext-theme "ui-code")

;  
  )

