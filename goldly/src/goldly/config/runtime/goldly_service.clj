(ns goldly.config.runtime.goldly-service)

(defn- aggregate [acc {:keys [clj-services]}]
  (if clj-services
    (conj acc clj-services)
    acc))

(defn clj-service-config [exts]
  (->> (reduce aggregate [] (vals exts))
       (into [])))

(comment
  (require '[goldly.ext.discover :refer [discover]])
  (->> (discover {:lazy true})
       (clj-service-config))

 ; 
  )
