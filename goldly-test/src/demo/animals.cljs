(ns demo.animals)

(def data {:bear {:color "brown" :size "big"}
           :snake {:color "varies" :shape "a cylinder"}})

(defn animals []
  data)

(defn all []
  (map (fn [[k v]] (name k)) data))


