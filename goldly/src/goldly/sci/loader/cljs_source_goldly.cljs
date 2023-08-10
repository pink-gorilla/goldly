(ns goldly.sci.loader.cljs-source-goldly
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [goldly.service.core :refer [run-cb]]
   [goldly.sci.loader.cljs-source :refer [ns->filename on-cljs-received]]))

(defn load-module-sci [{:keys [ctx libname ns opts property-path] :as d}]
  ; libname: bongo.trott ; the ns that gets compiled
  ; ns:  demo.notebook.applied-science-jsinterop ; the namespace that is using it
  ; opts: {:as bongo, :refer [saying]}
  ; ctx is the sci-context
  (info "load-sci-src" "libname:" libname "ns: " ns "opts:" opts)
  (let [filename (-> libname str ns->filename (str ".cljs"))]
    (info "loading filename: " filename)
    (js/Promise.
     (fn [resolve reject]
       (run-cb {:fun 'goldly.cljs.loader/load-file-or-res!
                :args [filename]
                :cb (partial on-cljs-received ctx libname ns opts resolve reject)
                :timeout 8000})))))
