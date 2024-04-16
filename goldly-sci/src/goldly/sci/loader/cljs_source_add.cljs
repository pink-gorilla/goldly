(ns goldly.sci.loader.cljs-source-add
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string]
   [promesa.core :as p]
   [sci.core :as sci]
   [sci.async :as scia]
   [goldly.sci.loader.cljs-source-load :refer [load-sci-cljs-code]]))

(defn valid-code? [{:keys [code] :as result}]
  (and code
       (not (clojure.string/blank? code))))

(defn add-sci-cljs-source [{:keys [ctx libname ns opts]}]
  (debug "add-sci-cljs-source libname:" libname " ns: " ns "opts: " opts)
  (let [r (p/deferred)
        code-p (load-sci-cljs-code libname)
        eval-p      (-> code-p
                        (p/then (fn [code]
                                  (scia/eval-string+ ctx code)))
                        (p/catch (fn [err]
                                   (p/reject! r {:load-error (str "no sci-code for ns: " libname " err: " err)}))))]
    (-> eval-p
        (p/then  (fn [res]
                   (if (valid-code? res)
                     (let [{:keys [val ns]} res]
                       (info "sci-cljs compile result: " res)
                       (when-let [as (:as opts)]
                                   ;; import class in current namespace with reference to globally
                                   ;; registed class
                         (warn "registering as: " as "in ns: " ns " to:" (symbol libname))
                         (sci/add-import! ctx ns (symbol libname) (:as opts)))
                       (p/resolve! r {:handled false}))
                     (do (error "no sci-code received for ns: " libname)
                         (p/reject! r {:load-error (str "no sci-code for ns: " libname)})))))
        (p/catch (fn [e]
                   (error "compile error for: " libname " error: " e)
                   (p/reject! r (str "compile error for: " libname)))))
    r))

