(ns dev.bundle-size
  (:require
   [clojure.string]
   [clojure.java.io :as io]
   [shadow.cljs.build-report])
  (:gen-class))


(defn -main []
  (shadow.cljs.build-report/generate :web
                                     {:print-table true
                                      :report-file "target/bundlesizereport.html"}))