(ns demo.test.reources
  (:require
   [modular.resource.explore :as e]
   [clojure.pprint :refer [print-table]]))

(defn describe [res-path]
  (->> (rs/resource-dir res-path)
       (map (partial describe-url res-path))))

(defn dir? [{:keys [dir?]}]
  dir?)

(->> (e/describe "goldly")
     ;(remove #(:dir? %))
     (print-table [:scheme
                   :name
                   ;:name-full
                   ;:dir?
                   ;:path
                   ]))
(defn get-data-readers*
  "returns a merged map containing all data readers defined by libraries
      on the classpath."
  ([]
   (get-data-readers* (. (Thread/currentThread) (getContextClassLoader))))
  ([^ClassLoader classloader]
   (let [data-reader-urls (enumeration-seq (. classloader (getResources "data_readers.cljc")))]
     data-reader-urls
;        (reduce load-data-reader-file {} data-reader-urls)
     )))
(-> (get-data-readers*)
    println)
