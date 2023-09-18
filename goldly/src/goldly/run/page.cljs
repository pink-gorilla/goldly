(ns goldly.run.page
  (:require
   [taoensso.timbre :as timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.sci.kernel-cljs :refer [require-async resolve-symbol]]))

(defn resolve-if-possible [s]
  (try
    (resolve-symbol s)
    (catch :default _ex
      nil)))

(defn get-page-fn [s]
  (if-let [fun (resolve-if-possible s)]
    fun
    (let [libspec [(-> s namespace symbol)]
          _ (info "get-page-fn requiring libspec: " libspec)
          require-p (require-async libspec)
          resolve-p (p/then require-p (fn [_require-result]
                                        (info "get-page-fn: lib loaded. resolving " s " .. ")
                                        (let [f (resolve-symbol s)]
                                          (info "get-page-fn resolved symbol: " s)
                                          (info "get-page-fn resolved fun: " f)
                                          f)))]
      (p/catch require-p (fn [require-error]
                           (error "get-page-fn: require result failure!")
                           (error "error: " require-error)
                           nil))
      resolve-p)))
