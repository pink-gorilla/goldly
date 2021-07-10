(ns goldly.extension.theme
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.config :refer [config-atom]]
   [goldly.extension.core :refer [ext-lazy? get-extension]]))

(defn theme-split [theme]
  (let [theme (or theme {})
        {:keys [available current]
         :or {available {}
              current {}}} theme]
    [available current]))

(defn merge-theme [global-theme module-theme]
  (let [[available-g current-g] (theme-split global-theme)
        [available-m current-m] (theme-split module-theme)]
    {:available (merge available-g available-m)
     :current (merge current-g current-m)}))

(defn add-extension-theme [{:keys [name theme]
                            :or {theme {:available {} :current {}}}
                            :as ext}]
  (if (ext-lazy? ext)
    (debug "not preloading css for lazy ext:" name)
    (let [theme-g (get-in @config-atom [:webly :theme])
          theme-m (merge-theme theme-g theme)]
      (debug "preloading css for ext: " name)
      (swap! config-atom assoc-in [:webly :theme] theme-m))))

(defn ext-theme [name]
  (if-let [ext (get-extension name)]
    (:theme ext)
    {:available {}
     :current {}
     :error (str "ext not found: " name)}))