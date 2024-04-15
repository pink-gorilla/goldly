(ns goldly.config.runtime.ring-api-handler)

(defn- aggregate-api-routes [acc-api-routes
                             {:keys [api-routes]
                              :or {api-routes {}}}]
  (merge acc-api-routes api-routes))

(defn ring-api-handler-config [exts]
  (reduce aggregate-api-routes {} (vals exts)))

(comment
  (require '[goldly.ext.discover :refer [discover]])
  (->> (discover {:lazy true})
       (ring-api-handler-config))

 ; 
  )
