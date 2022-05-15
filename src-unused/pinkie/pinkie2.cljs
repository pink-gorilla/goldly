(ns goldly.extension.pinkie2
  (:require-macros [goldly.extension.pinkie2 :as clj])
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug infof warn errorf]]
   [pinkie.pinkie :refer [register-tag]]
   [webly.build.lazy]))

(defn register-tag-safe [k v]
  (try
    (register-tag k v)
    (catch :default e
      (errorf "pinkie/register-tag failed for: %s" k)
      (errorf "exception: %s" e))))

(defn add-extension-pinkie-static []
  (let [pinkie (clj/registry)]
    (if (not (empty? pinkie))
      (do (errorf "adding %s pinkie renderers: %s " (count pinkie) (keys pinkie))
          (doall (for [[k v] pinkie]
                   ;(do ;(warn "pinky register: " k v (type v) (pr-str v))
                   (register-tag-safe k v)
                    ; )
                   )))
      (warn "no pinkie renderers in config received."))))
