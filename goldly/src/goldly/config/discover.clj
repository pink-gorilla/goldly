(ns goldly.config.discover
  (:require
   [clojure.edn :as edn]
   [resauce.core :as rs]
   [goldly.config.goldly :refer [lazy-excludes lazy-enabled]]))

;; config

(defn lazy-excluded? [goldly-config module-name]
  (let [excludes (lazy-excludes goldly-config)]
    (contains? excludes module-name)))

(defn ext-lazy-override [goldly-config {:keys [name lazy]
                                        :or {lazy false}
                                        :as ext}]
  ;(info "goldly lazy enabled:" (lazy-enabled goldly-config))
  ;(info "ext lazy excluded:" name (lazy-excluded? goldly-config name))
  (if (and (lazy-enabled goldly-config)
           (not (lazy-excluded? goldly-config name)))
    (assoc ext :lazy lazy)
    (assoc ext :lazy false)))

(defn add-extension [goldly-config ext-res-name]
  (->> ext-res-name
       slurp
       edn/read-string
       (ext-lazy-override goldly-config)))

(defn discover [goldly-config]
  (let [ext-res-names  (rs/resource-dir "ext")
        ext-list (map (partial add-extension goldly-config) ext-res-names)]
    (into {}
          (map (fn [ext]
                 [(:name ext) ext]) ext-list))))

(comment

  (require '[modular.resource.explore :as explore])
  (->> (explore/describe-files "")
       (clojure.pprint/print-table [:scheme :name]))

  (rs/resources "demo.notebook.goldly")

  (-> (rs/resources "")

      println)

  (-> (rs/resources "demo/notebook/apple.clj")
      first
      (rs/directory?))

  (-> (rs/resource-dir "ext")
      ;first
      last
      ;slurp
      )

  (-> (rs/resource-dir "demo/notebook")
      ;first
      last
      ;rs/name
                                        ;slurp
      )

  ;(recursive-resource-paths "ext")
  ;(recursive-resource-paths "")

  (->> (discover {:lazy true
                  ;:lazy-exclude #{"ui-gorilla"}
                  })
       vals
       (map (juxt :name :lazy)))

  (-> (discover {:lazy true
                  ;:lazy-exclude #{"ui-gorilla"}
                 })
      (write-extensions))

;  
  )
