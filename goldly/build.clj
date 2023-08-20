(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [modular.date :refer [now-str]]))

(def lib 'org.pinkgorilla/goldly)
(def version (format "0.4.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  (spit (doto (fs/file "resources/META-INF/pink-gorilla/goldly/meta.edn")
          (-> fs/parent fs/create-dirs)) {:module-name "goldly"
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
