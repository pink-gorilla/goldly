(ns goldly.services
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   ;[webly.build.lazy :as lazy]
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

(defn get-extension-info [name]
  (let [content (slurp ".webly/extensions.edn")
        extensions (edn/read-string content)
        e (first (filter #(= name (:name %)) extensions))]
    e))

(defn sci-bindings []
  (edn-load-res "public/sci-cljs-bindings.edn"))

(s/add {;:webly/lazy-list lazy/available this is cljs

        ; compile time
        :goldly/version #(load-version "goldly")
        :goldly/extension-summary extension-summary
        :goldly/extension-list extensions
        :goldly/sci-bindings sci-bindings

        :goldly/get-extension-info get-extension-info ; this is used by the lazy-extension css loader
        :goldly/get-extension-theme ext-theme

        ;runtime
        :goldly/services s/services-list
        :edn/load edn-load})

(comment
    ;compile time
  (load-version "goldly")
  (extension-summary)
  (extensions)
  (sci-bindings)

  (get-extension-info "ui-code")
  (ext-theme "ui-code")

  (s/services-list)

;  
  )

