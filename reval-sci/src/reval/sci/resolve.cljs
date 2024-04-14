(ns reval.goldly.viz.resolve
  (:require
   [goldly.sci :refer [require-async resolve-symbol-sci]]))

 [goldly.sci :refer [require-async resolve-symbol-sci]]

(defn require-s-sci [s]
  (let [libspec [(-> s namespace symbol)]
        require-p (require-async libspec)]
    (log "get-render-fn requiring libspec: " libspec)
    (resolve s)))
