(ns goldly.sci.compile
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info warn error]]
   [reagent.dom]
   [cljs.reader :refer [read-string]]
   [sci.core :as sci]

   [goldly.sci.bindings-static :refer [ns-static]]))

;; https://github.com/borkdude/sci
;; ClojureScript, even when compiled with :advanced, and (as a consequence) JavaScript


; compile code (system/html)

(defn compile-code [code ctx]
  (try
    (sci/eval-string code ctx)
    (catch :default e
      (error "compile-code --]" code "[-- ex: " e)
      nil)))

;; compile cljs function

(defn no-op-fun [f-name]
  (fn [& args]
    (warn "running function (that had a compile error) " f-name " (no-fun) ..")))

(defn run-fn [f-name fun]
  (fn [& args]
    (try
      (apply fun args)
      (catch :default e
        (error "error running fun " f-name " args: " args "ex: " e)))))

(defn compile-fn
  "compiles a system/fns. 
   On compile error returns no-op-fun"
  [ctx f-name f-body]
  (info "compile-fn " f-name " code: " f-body)
  (let [f-body (read-string f-body)
        fun (compile-code f-body ctx)
        _ (trace "fun: " fun)
        fun (if fun
              fun
              (do (error "compile error in system/fn " f-name)
                  (no-op-fun f-name)))]
    ;fun
    (run-fn f-name fun)))

(defn compile-fn-raw
  "compiles a system/fns. 
   On compile error returns no-op-fun"
  [ctx f-name f-body]
  (info "compile-fn " f-name  " code: " f-body)
  (let [;f-body (read-string f-body)
        fun (compile-code f-body ctx)
        _ (trace "fun: " fun)
        fun (if fun
              fun
              (do (error "compile error in system/fn " f-name)
                  (no-op-fun f-name)))]
    ;fun
    (run-fn f-name fun)))