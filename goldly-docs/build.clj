(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [deps-deploy.deps-deploy :as dd]))

(def lib 'org.pinkgorilla/goldly-docs)
(def version (format "0.4.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  (spit (doto (fs/file "resources/META-INF/pink-gorilla/goldly-docs/meta.edn")
          (-> fs/parent fs/create-dirs)) {:module-name "goldly-docs"
                                          :version version})
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

; (def lib 'io.github.pink-gorilla/webly)
;(def version (format "0.4.%s" (b/git-count-revs nil)))
;(def class-dir "target/classes")
;(def basis (b/create-basis {:project "deps.edn"}))
;(def jar-file (format "target/%s-%s.jar" (name lib) version))

#_(defn jar2 [_]
    (b/delete {:path "target"})
    (println "Producing jar:" jar-file)
    (spit (doto (fs/file "resources/META-INF/pink-gorilla/webly/meta.edn")
            (-> fs/parent fs/create-dirs)) {:module-name "webly"
                                            :version version})
    (b/write-pom {:class-dir class-dir
                  :lib lib
                  :version version
                  :basis basis
                  :scm {:url "http://github.com/pink-gorilla/webly"
                        :connection "scm:git:git://github.com/pink-gorilla/webly.git"
                        :developerConnection "scm:git:ssh://git@github.com/pink-gorilla/webly.git"}
                  :src-dirs ["src"]
                  :transitive true})
    (b/copy-dir {:src-dirs ["src"]
                 :target-dir class-dir})
    (b/jar {:class-dir class-dir
            :jar-file jar-file}))

