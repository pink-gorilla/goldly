(ns goldly.extension.cljs-autoload
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [goldly.extension.classpath :refer [describe-url]]
   [resauce.core :as rs]))

(defn get-ns-files [res-path]
  (->> (rs/resource-dir res-path)
       (remove rs/directory?)
       (map (partial describe-url res-path))))

(defn split-ext [filename]
  (let [m (re-matches #"(.*)\.(clj[sc]*)" filename)
        [_ name ext] m]
    [name ext]))

(defn is-format? [fmt [_ ext]] ; name
  (case ext
    "cljs" (= fmt :cljs)
    "clj" (= fmt :clj)
    "cljc" true))

(defn get-file-list [fmt res-path]
  (->> (get-ns-files res-path)
       (map :name)
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       ;(map #(filename->ns res-path %))
       ))
(defonce autoload-cljs-res-a (atom  []))

(defn get-cljs-res-files [s]
  ;(info "getting res files for path:" s)
  (let [path (if (str/ends-with? s "/")
               s
               (str s "/"))]
    (->> (get-file-list :cljs path)
         (map #(str path % ".cljs"))
         (into []))))

(defn add-extension-cljs-autoload [{:keys [name autoload-cljs-dir]
                                    :or {autoload-cljs-dir []}
                                    :as extension}]
  (doall (for [s autoload-cljs-dir]
           (let [paths (get-cljs-res-files s)]
             (info "discovered: " paths)
             (swap! autoload-cljs-res-a concat paths)))))

(comment
  (get-file-list :clj "demo/notebook/")
  (get-file-list :cljs "goldly/lib/")

  (get-cljs-res-files "goldly/lib/")
  (get-cljs-res-files "goldly/lib")
  (get-cljs-res-files "goldly/devtools")

  (add-extension-cljs-autoload
   {:name "bongo" :autoload-cljs-dir ["goldly/lib"]})

  @autoload-cljs-res-a
  (reset! autoload-cljs-res-a [])

;  
  )