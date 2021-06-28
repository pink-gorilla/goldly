(ns goldly.discover
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [fipp.clojure]
   [clojure.edn :as edn]
   [resauce.core :as rs]
   [webly.writer]
   [goldly.sci.bindings :refer [add-cljs-namespace goldly-namespaces goldly-bindings goldly-ns-bindings add-cljs-bindings add-cljs-ns-bindings]]
   [systems.snippet-registry :refer [add-snippet]]
   [pinkgorilla.repl :refer [add-require]]))

#_(defn resource-dir-paths [path]
    (let [parents (map str (rs/resources path))]
    ;(warn "parents: " (pr-str parents))
      (for [url (rs/resource-dir path)]
        (let [prefix (first (filter (partial str/starts-with? url) parents))]
          (str path (subs (str url) (count (str prefix))))))))

#_(defn resource-dir [path]
    (if (not (= path "module-info.class")) ; (rs/directory? path))
      (rs/resource-dir path)
      []))

#_(defn recursive-resource-paths [path]
    (tree-seq (comp seq resource-dir) resource-dir-paths path))

(defn add-extension [{:keys [name snippets
                             cljs-namespace
                             cljs-bindings cljs-ns-bindings
                             clj-require]
                      :or {snippets []
                           cljs-namespace []
                           cljs-bindings {}
                           cljs-ns-bindings {}
                           clj-require []}
                      :as extension}]
  (info "adding extension: " name)

  (debug "cljs ns: " cljs-namespace)
  (doall (for [n cljs-namespace]
           ;(add-cljs-namespace [n])
           (swap! goldly-namespaces conj [n])))

  (debug "cljs bindings: " cljs-bindings)
  ;(add-cljs-bindings cljs-bindings)
  (swap! goldly-bindings merge cljs-bindings)

  (debug "cljs ns-bindings: " cljs-ns-bindings)
  ;(doall (for [[k v] cljs-ns-bindings]
  ;         (add-cljs-ns-bindings k v)))
  (swap! goldly-ns-bindings merge cljs-ns-bindings)

  (debug "clj require: " clj-require)
  (doall (for [r clj-require]
           (add-require r)))

  (doall (for [s snippets]
           (add-snippet s))))

(defn pr-str-fipp [config]
  (with-out-str
    (fipp.clojure/pprint config {:width 40})))

(defn save-extensions [extensions]
  (webly.writer/ensure-directory-webly)
  (->> (pr-str-fipp extensions)
       (spit ".webly/extensions.edn")))

(defn discover []
  (let [r  (rs/resource-dir "ext")
        extensions (for [f r]
                     (-> f slurp edn/read-string))]
    (warn "discovered extensions: " (pr-str r))
    (save-extensions extensions)
    (doall (for [ext extensions]
             (add-extension ext)))))

(comment

  (rs/resources "ext")
  (rs/resources "")

  (-> (rs/resource-dir "ext")
      ;first
      last
      ;slurp
      )
  (recursive-resource-paths "ext")
  (recursive-resource-paths "")

  (map str [])
  ;  
  )