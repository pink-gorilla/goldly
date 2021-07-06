(ns goldly.extension.pinkie
  (:require-macros [goldly.extension.pinkie :as clj])
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug infof warn error]]
   [pinkie.pinkie :refer [register-tag]]))

; this approach does not work. functions are not available at runtime.
#_(defn add-extension-pinkie [pinkie]
    (if (not (empty? pinkie))
      (do (warn "pinkie-add: " pinkie)
          (doall (for [[k v] pinkie]
                   (do (warn "pinky register: " k v (type v) (pr-str v))
                       (register-tag k v)))))
      (warn "no pinkie renderers in config received.")))

(defn add-extension-pinkie-static []
  (let [pinkie (clj/registry)]
    (if (not (empty? pinkie))
      (do (infof "adding %s pinkie renderers: %s " (count pinkie) (keys pinkie))
          (doall (for [[k v] pinkie]
                   (do ;(warn "pinky register: " k v (type v) (pr-str v))
                     (register-tag k v)))))
      (warn "no pinkie renderers in config received."))))
