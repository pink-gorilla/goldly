(ns build
  (:require
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
))

(def lib 'org.pinkgorilla/goldly)
(def version (format "0.7.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "pom-template.xml"
             :transitive true)
      (bb/jar)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib
             :version version)
      (bb/deploy)))

;If you are working in a monorepo, such as the [Polylith architecture](https://polylith.gitbook.io/), and need
;to build library JAR files from projects that rely on `:local/root` dependencies to specify other source
;components, you will generally want to pass `:transitive true` to the `jar` task.

;Without `:transitive true`, i.e., by default, the `jar` task generates a `pom.xml` from just the dependencies
;specified directly in the project `deps.edn` and does not consider dependencies from local source subprojects.
;In addition, by default `jar` only copies `src` and `resources` from the current project folder.

;With `:transitive true`, the `jar` task includes direct dependencies from local source subprojects when
;generating the `pom.xml` and will also copy all folders found on the classpath -- which is generally all
;of the `src` and `resources` folders from those local source subprojects.


