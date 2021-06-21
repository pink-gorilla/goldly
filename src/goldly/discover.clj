(ns goldly.discover
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [resauce.core :as rs]
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

  (info "cljs ns: " cljs-namespace)
  (doall (for [n cljs-namespace]
           ;(add-cljs-namespace [n])
           (swap! goldly-namespaces conj [n])))

  (info "cljs bindings: " cljs-bindings)
  ;(add-cljs-bindings cljs-bindings)
  (swap! goldly-bindings merge cljs-bindings)

  (info "cljs ns-bindings: " cljs-ns-bindings)
  ;(doall (for [[k v] cljs-ns-bindings]
  ;         (add-cljs-ns-bindings k v)))
  (swap! goldly-ns-bindings merge cljs-ns-bindings)

  (info "clj require: " clj-require)
  (doall (for [r clj-require]
           (add-require r)))

  (doall (for [s snippets]
           (add-snippet s))))

(defn discover []
  (let [r (->> (rs/resource-dir "ext")
               ;(rs/resources "")
               ;(filter #(str/ends-with? % "/gorilla-ext.edn"))
               ;doall
               )]
    (warn "discovered extensions: " (pr-str r))
    (doall (for [f r]
             (let [ext  (-> f slurp edn/read-string)] ; io/resource
               (add-extension ext))))))

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