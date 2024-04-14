(ns goldly.config.info)

(defn extension-summary [ext-list]
  (let [summary  (into []
                       (map
                        (fn [e]
                          (select-keys e [:name :lazy]))
                        ext-list))]
    summary))






