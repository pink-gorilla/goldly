(ns goldly.config.build.cljs-namespaces)

(defn- add-extension-cljs [webly-modules
                           {:keys [lazy name cljs-namespace]
                            :or {cljs-namespace []}
                            :as ext}]

  (if (empty? cljs-namespace)
    webly-modules
    (merge webly-modules
           (if lazy
             {(keyword name) cljs-namespace}
             {:goldly-main (concat (or (:goldly-main webly-modules) [])
                                   cljs-namespace)}))))

(defn create-cljs-namespaces-config [exts]
  (reduce add-extension-cljs {} (vals exts)))

(comment
  (require '[goldly.config.discover :refer [discover]])
  (->> (discover {:lazy true})
       (create-cljs-namespaces-config))

; 
  )
