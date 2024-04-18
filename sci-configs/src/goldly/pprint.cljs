(ns goldly.pprint
  (:require
   [cljs.pprint :as pp]
   [sci.core :as sci]))

(defn pprint [& args]
  (binding [*print-fn* @sci/print-fn]
    (apply pp/pprint args)))

(defn print-table [& args]
  (binding [*print-fn* @sci/print-fn]
    (apply pp/print-table args)))

(def pns (sci/create-ns 'cljs.pprint nil))

(def pprint-namespace
  {'pprint (sci/copy-var pprint pns)
   'print-table (sci/copy-var print-table pns)})

;   {:namespaces {'cljs.pprint pprint-namespace}}

; (:require [cljs.pprint :as pprint]))
; (pprint/cl-format nil  "~,2f" 1.2345) ; => returns "1.23"
; (pprint/cl-format true "~,2f" 1.2345) ; => prints "1.23", returns nil