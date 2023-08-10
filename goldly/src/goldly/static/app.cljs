(ns goldly.static.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn]]
   [goldly.sci.kernel-cljs :refer [require-async resolve-symbol]]))

; required in glodly.app.build

(defn mount-app [page-fn]
  (reagent.dom/render
   [page-fn]
   (.getElementById js/document "app")))

(defn ^:export start [symbol-page-as-string]
  (enable-console-print!)
  (println "starting goldly static app page symbol:" symbol-page-as-string)
  (let [page-symbol (symbol symbol-page-as-string)
        libspec (-> page-symbol namespace symbol)
        require-p (require-async libspec)]
    (.then require-p (fn [res]
                       (let [page-fn (resolve-symbol page-symbol)]
                         (if page-fn
                           (mount-app page-fn)
                           (println "could not resolve page: " page-symbol)))))
    (.catch require-p (fn [err]
                        (println "could not sci-require ns: " libspec)))))

