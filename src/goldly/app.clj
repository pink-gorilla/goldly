(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [goldly.web.server :as web]
   [goldly.puppet.loader :refer [load-components-namespaces require-components]]
   [goldly.puppet.db :refer [systems-response]])
  (:gen-class))

(defn goldly-run!
  "This starts goldly (web server, user defined systems,...)"
  [{:keys [port
           app-systems-ns
           user-systems-dir]
    :or {port 8000
         ;app-systems-dir "./src/systems/"
         app-systems-ns '[systems.help
                          systems.components
                          systems.snippets]}}]
  ;(system-start! components)
  (when app-systems-ns
    (println "loading app systems from: " app-systems-ns)
    ;(load "address_book/core") ; this works with classpath
    (load-components-namespaces app-systems-ns))
  (when user-systems-dir
    (println "loading user systems from: " user-systems-dir)
    (require-components user-systems-dir))
  (web/server-start! {:port port}))

(defn -main [& args]
  (println "goldly app starting with cli-args: " args)
  (let [user-systems-dir (first args)]
    (goldly-run! {:user-systems-dir user-systems-dir}))
  (println "goldly started successfully. systems running: " (systems-response)))

(comment
  (goldly-run! {})
  (-main)

 ;
  )