(ns goldly.run.service
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [goldly.service.expose :refer [start-services]]))

(defn expose-extension-clj-services [clj-services]
  (info "starting extensions clj-services ..")
  (doall
   (map start-services clj-services)))