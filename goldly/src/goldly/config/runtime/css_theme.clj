(ns goldly.config.runtime.css-theme)

(defn- theme-split [theme]
  (let [theme (or theme {})
        {:keys [available current]
         :or {available {}
              current {}}} theme]
    [available current]))

(defn- merge-theme [global-theme module-theme]
  (let [[available-g current-g] (theme-split global-theme)
        [available-m current-m] (theme-split module-theme)]
    {:available (merge available-g available-m)
     :current (merge current-g current-m)}))

(defn- extension-css-theme [themes
                            {:keys [lazy theme]
                             :or {theme {:available {} :current {}}}}]
  (if lazy
    themes
    (merge-theme themes theme)))

(defn css-theme-config [exts]
  (reduce extension-css-theme {:available {} :current {}} (vals exts)))

(comment
  (require '[goldly.ext.discover :refer [discover]])
  (->> (discover {:lazy false})
       (css-theme-config))

 ; 
  )



