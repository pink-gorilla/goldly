(ns goldly.static.build
  (:require
   [taoensso.timbre :refer [debug info infof warn error]]
   [clojure.java.io :as io]
   [babashka.fs :as fs]
   [modular.resource.load :refer [write-resources-to]]
   [goldly.config.runtime :refer [runtime-config]]
   [hiccup.page :as page]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(def static-root "target/static/")

(defn- ensure-static-directory [rel-path]
  (ensure-directory (str static-root rel-path)))

(defn export-path [rel-to-path res-path]
  (let [to-path (str static-root rel-to-path)]
    (info "writing resource path: " res-path)
    (write-resources-to to-path res-path)))

(defn export-sci-cljs [sci-cljs-dirs]
  (info "exporting sci-cljs in dirs: " sci-cljs-dirs)
  (ensure-static-directory "code")
  (doall
   (map (partial export-path "code")
        sci-cljs-dirs)))

(defn app-page-static [init-ns]
  (page/html5
   {:mode :html}
   [:head
    [:meta {:http-equiv "Content-Type"
            :content "text/html; charset=utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:meta {:name "description"
            :content "webly app"}]
    [:meta {:name "author"
            :content "pink-gorilla"}]
              ; <meta name= "keywords" content= "keywords,here" >
    [:title "goldly app"]]
   [:body
    [:script {:src (str "r/" "webly.js")
              :type "text/javascript"
              :onload (str "goldly.static$.app.start ('" init-ns "');")}]
    [:div#app]]))

(defn create-static-html [init-ns]
  (let [html (app-page-static init-ns)
        filename (str static-root "index.htm")]
    (info "writing static page: " filename)
    (spit filename html)))

(defn goldly-build-static [goldly-config page-symbol sci-cljs-dirs]
  (let [rconfig (runtime-config goldly-config)
        {:keys [cljs-autoload-dirs]} rconfig]
    (ensure-directory "target")
    (fs/delete-tree (str static-root))
    (ensure-directory "target/static")
    ; export cljs-bundle
    ; export public resources (from any jar or file-path)
    (export-path "" "public")

    (fs/move (str static-root "public")
             (str static-root "r")
             {:replace-existing true})
    ; export sci-cljs files
    (export-sci-cljs (concat cljs-autoload-dirs ; extension autoload
                             (or (:autoload-cljs-dir goldly-config) [])
                             (or sci-cljs-dirs []))) ; goldly app autoload
    ; generate static page
    ; generate startup script
    (create-static-html page-symbol)))

(comment
  (require '[modular.config :refer [get-in-config]])
  (def goldly-config (get-in-config [:goldly]))
  (info "goldly config: " goldly-config)
  (:autoload-cljs-dir goldly-config)
  (def goldly-runtime-config (runtime-config goldly-config))
  (info "goldly runtime config: " goldly-runtime-config)
  (keys goldly-runtime-config)
  (:cljs-autoload-dirs goldly-runtime-config)
  (goldly-build-static goldly-config)

;
  )
