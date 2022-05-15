(ns goldly.run.services
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [goldly.service.core :as s]
   [goldly.service.handler] ; side effects
   [goldly.version :refer [load-version]]
   ;[goldly.extension.core :refer [extension-summary extensions]]
   ))

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

(s/add {;:webly/lazy-list lazy/available this is cljs

        ; edn-loader
        :edn/load edn-load

        ; used in devtools:
        :goldly/version #(load-version "goldly")
        ;
        ;:goldly/extension-summary extension-summary
        ;:goldly/extension-list extensions
        :goldly/build-sci-config build-sci-config
        :goldly/run-sci-cljs-autoload run-sci-cljs-autoload

        ; this is used by the lazy-extension css loader
        :goldly/get-extension-info get-extension-info

        ;runtime
        :goldly/services s/services-list})

(comment
    ;compile time
  (load-version "goldly")
  ;(extension-summary)
  ;(extensions)
  (sci-bindings)

  (get-extension-info "ui-code")
  ;(ext-theme "ui-code")

  (s/services-list)

;  
  )

