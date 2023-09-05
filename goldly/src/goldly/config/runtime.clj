(ns goldly.config.runtime
  (:require
   [goldly.config.discover :refer [discover]]
   [goldly.config.runtime.css-theme :refer [css-theme-config]]
   [goldly.config.runtime.clj-require :refer [clj-require-config]]
   [goldly.config.runtime.cljs-autoload :refer [cljs-autoload-config]]
   [goldly.config.runtime.cljs-autoload-files :refer [cljs-autoload-files]]
   [goldly.config.runtime.ring-api-handler :refer [ring-api-handler-config]]))

(defn runtime-config [goldly-config]
  (let [exts (discover goldly-config)
        cljs-autoload-dirs  (cljs-autoload-config exts)]
    {:exts exts
     :clj-require (clj-require-config exts)
     :css-theme (css-theme-config exts)
     :cljs-autoload-dirs cljs-autoload-dirs
     :cljs-autoload-files (cljs-autoload-files cljs-autoload-dirs)
     :api-routes (ring-api-handler-config exts)}))

(comment
  (-> (runtime-config {:lazy false})
      ;keys
      ;:cljs-autoload-dirs
      :cljs-autoload-files)

;  
  )