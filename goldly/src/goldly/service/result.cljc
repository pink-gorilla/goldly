(ns goldly.service.result
  (:require
   [clojure.walk :as walk]
   [com.rpl.specter :refer [transform setval END]]
   #?(:clj  [taoensso.timbre  :refer [debug debugf info infof warn error errorf]]
      :cljs [taoensso.timbre :refer-macros [debug debugf info infof warn error errorf]])))

(defn specter-resolve
  [specter-vector]
  (walk/prewalk
   (fn [x] (if (keyword? x)
             (case x
               :END END
               x)
             x))
   specter-vector))

(defn update-atom-where [a where result]
  (debugf "updating atom where: %s with result: %s" where result)
  (try
    (let [where-resolved (specter-resolve where)]
      (debug "specter resolved: " where-resolved)
      (reset! a (setval where-resolved result @a)))
    (catch #?(:cljs :default
              :clj Exception) e
      (errorf "update-atom-where: %s  ex: %s" e))))