(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.config :refer [get-in-config]]
   [goldly.runner.clj-fn] ; side-efects
   [goldly.notebook.picasso] ; side-efects
   [goldly.puppet.loader :refer [load-components-namespaces]]))

(defn goldly-run! []
  (let [{:keys [systems-ns]}
        (get-in-config [:goldly])]
    (when systems-ns
      (info "loading systems from ns: " systems-ns)
      (load-components-namespaces systems-ns))))


