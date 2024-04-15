(ns goldly.sci.loader.shadow-load
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [webly.module.build :refer [load-namespace]]))

(defn load-shadow-ns [libname]
  (info "webly-shadow-ns load: " libname " type: " (type libname)) 
  (load-namespace libname))
