(ns goldly.config.runtime.clj-require)

(defn- extension-clj-require [clj-requires
                              {:keys [autoload-clj-ns]
                               :or {autoload-clj-ns []}}]
  (concat clj-requires autoload-clj-ns))

(defn clj-require-config [exts]
  (into []
        (reduce extension-clj-require [] (vals exts))))

(comment
  (require '[goldly.ext.discover :refer [discover]])
  (->> (discover {:lazy true})
       (clj-require-config))

 ; 
  )
