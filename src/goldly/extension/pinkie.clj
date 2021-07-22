(ns goldly.extension.pinkie
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.writer :refer [write-status]]))

(defonce pinkie-atom (atom {}))

(defn available []
  (keys @pinkie-atom))

(defmacro registry []
  @pinkie-atom)

(defn save-pinkie []
  (write-status "pinkie" @pinkie-atom))

(defn add-extension-pinkie [{:keys [name
                                    pinkie]
                             :or {pinkie {}}
                             :as extension}]

  (when (not (empty? pinkie))
    (debug "pinkie-add: " pinkie)
    (swap! pinkie-atom merge pinkie)))

(defn make-lazy [bindings]
  (let [lazy-bindings (into {}
                            (map (fn [[k v]]
                                   [k (list 'webly.build.lazy/wrap-lazy v)]) bindings))]
    (debug "make-lazy pinkie: " lazy-bindings)
    lazy-bindings))
(defn add-extension-pinkie-lazy [{:keys [name
                                         pinkie]
                                  :or {pinkie {}}
                                  :as extension}]

  (when (not (empty? pinkie))
    (debug "pinkie-add-lazy: " pinkie)
    (swap! pinkie-atom merge (make-lazy pinkie))))

