(ns goldly.sci.loader.cljs-source-load
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string]
   [ajax.core :refer [GET]]
   [webly.spa.mode :refer [get-mode get-resource-path]]))

(defn ns->filename [ns]
  (-> ns
      (clojure.string/replace #"\." "/")
      (clojure.string/replace #"\-" "_")))

(defn filename-dynamic [filename]
  (str "/code/" filename))

(defn filename-static [filename]
  (str (get-resource-path) "/code/" filename))

(defn ns->url [ns]
  (let [file (ns->filename ns)
        mode (get-mode)
        url (case mode
              :static (filename-static file)
              :dynamic (filename-dynamic file)
              (filename-dynamic file))
        url (str url ".cljs")]
    (info "loading sci-cljs source file from url: " url)
    url))


(defn load-sci-cljs-code [libname]
  ; libname: bongo.trott ; the ns that gets compiled
  (info "load-sci-cljs-code" "libname:" libname )
  (let [url (-> libname str ns->url)]
    (GET url)))
