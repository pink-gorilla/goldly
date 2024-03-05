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
             :src-pom "template-pom.xml"
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

#_(def pom-template
  [[:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "https://www.eclipse.org/legal/epl-v10.html"]]]
   [:developers
    [:developer
     [:name "pink-gorilla"]]]
   [:scm
    [:url "https://github.com/pink-gorilla/goldly"]
    [:connection "scm:git:git://github.com/pink-gorilla/goldly.git"]
    [:developerConnection "scm:git:ssh://git@github.com/pink-gorilla/goldly.git"]]])

;If you are working in a monorepo, such as the [Polylith architecture](https://polylith.gitbook.io/), and need
;to build library JAR files from projects that rely on `:local/root` dependencies to specify other source
;components, you will generally want to pass `:transitive true` to the `jar` task.

;Without `:transitive true`, i.e., by default, the `jar` task generates a `pom.xml` from just the dependencies
;specified directly in the project `deps.edn` and does not consider dependencies from local source subprojects.
;In addition, by default `jar` only copies `src` and `resources` from the current project folder.

;With `:transitive true`, the `jar` task includes direct dependencies from local source subprojects when
;generating the `pom.xml` and will also copy all folders found on the classpath -- which is generally all
;of the `src` and `resources` folders from those local source subprojects.


