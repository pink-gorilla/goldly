(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [deps-deploy.deps-deploy :as dd]
   [modular.date :refer [now-str]]))

(def lib 'org.pinkgorilla/goldly-docs)
(def version (format "0.4.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))


(def pom-template
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

(defn- spit-version []
  (spit (doto (fs/file "target/classes/META-INF/pink-gorilla/goldly-docs/meta.edn")
          (-> fs/parent fs/create-dirs)) {:module-name "goldly-docs"
                                          :version version
                                          :generated (now-str)
                                          }))


(def opts {:class-dir class-dir
           :lib lib
           :version version
           :basis basis
           :pom-data pom-template
           :transitive true
           :src-dirs ["src"]})

(defn jar [_]
  (b/write-pom opts)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (spit-version)
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn deploy "Deploy the JAR to Clojars." [_]
  (println "Deploying to Clojars..")
  (dd/deploy {:installer :remote
              ;:sign-releases? true
              :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))
              ;:artifact "target/tech.ml.dataset.jar"
              :artifact (b/resolve-path jar-file)}))


