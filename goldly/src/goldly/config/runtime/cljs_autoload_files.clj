(ns goldly.config.runtime.cljs-autoload-files
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [modular.resource.explore  :as resources]))

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
  (->> (resources/describe-files res-path)
       (map :name) ; :name-full
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       ;(map #(filename->ns res-path %))
       ))

(defn get-cljs-res-files [s]
  ;(info "getting res files for path:" s)
  (let [path (if (str/ends-with? s "/")
               s
               (str s "/"))]
    (->> (get-file-list :cljs path)
         (map #(str path % ".cljs"))
         (into []))))

(defn cljs-autoload-files  [autoload-cljs-dirs]
  (->> (map get-cljs-res-files autoload-cljs-dirs)
       (apply concat)
       (into [])))

(comment
  (get-file-list :clj "demo/notebook/")
  (get-file-list :cljs "goldly/lib/")

  (get-cljs-res-files "goldly/lib/")
  (get-cljs-res-files "goldly/lib")
  (get-cljs-res-files "goldly/devtools")

  (cljs-autoload-files ["goldly/lib"])

  (require '[goldly.config.discover :refer [discover]])
  (require '[goldly.config.runtime.cljs-autoload :refer [cljs-autoload-config]])

  (->> (discover {:lazy true})
       (cljs-autoload-config)
       (cljs-autoload-files))

  (explore-once "demo/cljs_libs")
  (explore-once "demo")

;  
  )