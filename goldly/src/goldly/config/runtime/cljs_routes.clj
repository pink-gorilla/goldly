(ns goldly.config.runtime.cljs-routes)

(defn- aggregate-routes [acc
                         {:keys [cljs-routes]
                          :or {cljs-routes {}}}]
  (merge acc cljs-routes))

(defn cljs-routes-config [exts]
  (reduce aggregate-routes {} (vals exts)))

(comment
  (require '[goldly.ext.discover :refer [discover]])
  (->> (discover {:lazy true})
       (cljs-routes-config))

 ; 
  )
