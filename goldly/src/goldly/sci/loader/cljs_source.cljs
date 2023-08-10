(ns goldly.sci.loader.cljs-source
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string]
   [sci.core :as sci]
   [sci.async :as scia]))

(defn ns->filename [ns]
  (-> ns
      (clojure.string/replace #"\." "/")
      (clojure.string/replace #"\-" "_")))

(defn valid-code? [{:keys [code] :as result}]
  (and code
       (not (clojure.string/blank? code))))

(defn on-cljs-received [ctx libname ns opts resolve reject [event-type {:keys [result] :as data}]]
  (debug "on-cljs-received: " event-type "data: " data)
  (if (valid-code? result)
    (let [code (:code result)
          eval-p (scia/eval-string+ ctx code)]
      (.then eval-p (fn [res]
                      (let [{:keys [val ns]} res]
                        (info "sci-cljs compile result: " res)
                        (when-let [as (:as opts)]
                             ;; import class in current namespace with reference to globally
                             ;; registed class
                          (warn "registering as: " as "in ns: " ns " to:" (symbol libname))
                          (sci/add-import! ctx ns (symbol libname) (:as opts)))
                        (resolve {:handled false}))))
      (.catch eval-p (fn [e]
                       (error "compile error for: " libname " error: " e)
                       (reject "compile error for: " libname))))
    (reject "no sci-code for: " libname)))
