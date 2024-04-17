(ns reval.kernel.cljs-sci
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [taoensso.timbre :refer [debug info warnf error]]
   [goldly.sci :refer [compile-code-async]]
   [goldly.sci.error :refer [exception->error]]
   [reval.type.sci]; side-effects
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.viz.data :refer [value->data]]))

(defn viz-adjust [er]
  (when [er] ; {:id :code :value :err :out :ns}
    (let [er-h (-> er
                   (dissoc :value)
                   (merge (value->data (:value er)))
                   (assoc :out (js->clj (:out er))))]
      er-h)))

(defn err-adjust [e]
  (when-let [sci-err (exception->error e)]
    {:sci-compilation-error true
     :err-sci sci-err}))

(defonce cur-ns (r/atom "user"))

(defn eval-cljs [{:keys [code] :as segment}]
  (let [r-p (p/deferred)
        segment (merge segment {:ns @cur-ns})
        _ (info "eval cljs: " segment)
        compile-p (compile-code-async code)]
    (-> compile-p
        (p/then  (fn [r]
                   (info "eval result: " r)
                   (reset! cur-ns (:ns r))
                   (p/resolve! r-p (viz-adjust r))))
        (p/catch (fn [err]
                   (p/reject! r-p (err-adjust err)))))
    r-p))

(defmethod kernel-eval :cljs [seg]
  (eval-cljs seg))
