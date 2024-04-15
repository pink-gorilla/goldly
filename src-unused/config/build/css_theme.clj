(ns goldly.config.build.css-theme)

; the lazy-css loader loads the entire css for an extension, when this
; extension gets lazy-loaded.

; therefore we need to have a map of key:module-name => value module-css-theme

; this is different to the webly static loaded css themes
; which is run at run-time.

(defn- extension-css-theme [themes
                            {:keys [lazy lazy-sci name theme]
                             :or {theme {:available {} :current {}}}}]
  (if (or lazy lazy-sci)
    (assoc themes name theme)
    themes))

(defn css-theme-config [exts]
  (reduce extension-css-theme {} (vals exts)))

(comment
  (require '[goldly.config.discover :refer [discover]])

  (->> (discover {:lazy true})
       (css-theme-config))

 ; 
  )



