(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [deps-deploy.deps-deploy :as dd]
   [modular.date :refer [now-str]]))

(def lib 'org.pinkgorilla/goldly-docs)
(def version (format "0.4.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  (spit (doto (fs/file "resources/META-INF/pink-gorilla/goldly-docs/meta.edn")
          (-> fs/parent fs/create-dirs)) {:module-name "goldly-docs"
                                          :version version
                                          :generated (now-str)})
  (-> opts
      (assoc :lib lib
             :version version
             :transitive true)
      ;(bb/run-tests)
      ;(bb/clean)
      (bb/jar)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib
             :version version)
      (bb/deploy)))



