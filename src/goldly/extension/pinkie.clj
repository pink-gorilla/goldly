(ns goldly.extension.pinkie
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]))

(defonce pinkie-atom (atom {}))
(defn add-extension-pinkie [{:keys [name
                                    pinkie]
                             :or {pinkie {}}
                             :as extension}]

  (when (not (empty? pinkie))
    (debug "pinkie-add: " pinkie)
    (swap! pinkie-atom merge pinkie)))

(defmacro registry []
  @pinkie-atom)