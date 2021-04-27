(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.config :refer [load-config! get-in-config]]
   [webly.user.app.app :refer [webly-run!]]

   [goldly.runner.clj-fn] ; sude-efects
   [goldly.puppet.loader :refer [load-components-namespaces require-components]]
   [goldly.puppet.db :refer [systems-response]]))

(defn goldly-run! []
  (let [{:keys [app-systems-ns user-systems-dir]
         :or {;app-systems-dir "./src/systems/"
              app-systems-ns '[systems.help
                              ;systems.components
                              ;systems.snippets
                               systems.login
                               systems.greeter
                               systems.click-counter]}}
        (get-in-config [:goldly])]
  ;(system-start! components)
    (when app-systems-ns
      (println "loading app systems from: " app-systems-ns)
    ;(load "address_book/core") ; this works with classpath
      (load-components-namespaces app-systems-ns))
    (when user-systems-dir
      (println "loading user systems from: " user-systems-dir)
      (require-components user-systems-dir))))


