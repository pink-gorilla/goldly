(ns bundel-config
  (:require
   [taoensso.timbre :refer [info]]

   [clojure.edn :as edn]
   [com.rpl.specter :as s]
   [fipp.clojure]))

(defn pr-str-fipp [config]
  (with-out-str
    (fipp.clojure/pprint config {:width 40})))

(defn load-deps-path [file path]
  (-> (slurp file)
      (edn/read-string)
      (get-in path)))

(defn generate-bundle-config [& args]
  (let [core (load-deps-path "deps.edn" [])
        bundel-deps (load-deps-path "profiles/bundel/deps.edn" [:deps])
        ;bundel-deps (dissoc bundel-deps 'org.pinkgorilla/goldly)
        ;bundel (get-in core [:aliases :notebook :extra-deps])
        ]
    (info "bundel deps: " bundel-deps)
    (->> (s/transform [:deps] #(merge % bundel-deps) core)
         (pr-str-fipp)
         (spit "profiles/bundelci/deps.edn"))))

(comment
  (generate-bundle-config)

  ;
  )
