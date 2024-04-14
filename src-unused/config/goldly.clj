(ns goldly.config.goldly)

(defn lazy-enabled [goldly-config]
  (or (get-in goldly-config [:lazy]) false))

(defn lazy-excludes [goldly-config]
  (or (get-in goldly-config [:lazy-exclude]) #{}))