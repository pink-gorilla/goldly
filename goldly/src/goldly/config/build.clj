(ns goldly.config.build
  (:require
   [goldly.config.discover :refer [discover]]
   [goldly.config.build.cljs-namespaces :refer [create-cljs-namespaces-config]]
   [goldly.config.build.sci-bindings :refer [sci-bindings-config]]
   [goldly.config.build.css-theme :refer [css-theme-config]]))

(defn build-config [goldly-config]
  (let [exts (discover goldly-config)]
    {:exts exts
     :cljs (create-cljs-namespaces-config exts)
     :sci (sci-bindings-config goldly-config exts)
     :css-theme (css-theme-config exts)}))

(comment

  (let [goldly-config {:lazy true}]
    (->> (discover goldly-config)
         (create-cljs-namespaces-config)
         ;(sci-bindings-config goldly-config)
                                        ;:ns-bindings
         ))
  (-> (build-config {:lazy true})
;     keys
      :cljs
      keys
      ;:cljs-namespace
      ;:sci
      ;keys
     ; :cljs-require
      ;:css-theme
      )
;  
  )
