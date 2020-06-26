(ns goldly.user.secrets.core
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]))

#_(defn creds
    "Get creds from environment or file. See sample-creds.edn for an example"
    []
    (if-let [github-token (System/getenv "GITHUB_TOKEN")]
      {:github-token github-token
       :gist-id      (System/getenv "GIST_ID")}
      (-> (io/resource "creds.edn")
          (if (do (slurp)
                  (edn/read-string))
            {}))))

(defn secrets
  "Get creds from secrets from file. See sample-creds.edn for an example"
  ([] (secrets "creds.edn"))
  ([resource-name]
   (let [r (io/resource resource-name)]
     (if r
       (-> r
           (slurp)
           (edn/read-string))
       {}))))

(comment
  (secrets)
;  
  )