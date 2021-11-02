(ns goldly.extension.pinkie
  (:require-macros [goldly.extension.pinkie :as clj])
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug infof warn errorf]]
   [pinkie.pinkie :refer [register-tag]]
   [webly.build.lazy]))

(defn add-extension-pinkie-static []
  (let [pinkie (clj/registry)]
    (if (not (empty? pinkie))
      (do (errorf "adding %s pinkie renderers: %s " (count pinkie) (keys pinkie))
          (doall (for [[k v] pinkie]
                   ;(do ;(warn "pinky register: " k v (type v) (pr-str v))
                   (register-tag k v)
                    ; )
                   )))
      (warn "no pinkie renderers in config received."))))
