(ns goldly.config.runtime.cljs-autoload)

(defn- extension-cljs-autoload [cljs-autoload-dirs
                                {:keys [autoload-cljs-dir]
                                 :or {autoload-cljs-dir []}}]
  (concat cljs-autoload-dirs autoload-cljs-dir))

(defn cljs-autoload-config [exts]
  (into []
        (reduce extension-cljs-autoload [] (vals exts))))

(comment
  (require '[goldly.config.discover :refer [discover]])
  (->> (discover {:lazy true})
       (cljs-autoload-config))

 ; 
  )
