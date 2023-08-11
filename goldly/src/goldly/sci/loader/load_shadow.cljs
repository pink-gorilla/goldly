(ns goldly.sci.loader.load-shadow
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [shadow.lazy :as lazy]))

; https://code.thheller.com/blog/shadow-cljs/2019/03/03/code-splitting-clojurescript.html
; https://clojureverse.org/t/shadow-lazy-convenience-wrapper-for-shadow-loader-cljs-loader/3841

; (def xy (lazy/loadable [demo.thing/x demo.other/y]))
; (def xym (lazy/loadable {:x demo.thing/x
;                         :y demo.other/y}))
; (def x (lazy/loadable snippets.snip/add))

(defn load-ext-shadow [loadable]
  (let [all-mods (.-modules loadable)]
    (info "shadow loadable mods: " all-mods) ; ["cljs-pprint-frisk"]
    (js/Promise.
     (fn [resolve reject]
       (let [on-success (fn [mod]
                          (info "shadow module-ns did load: " mod)
                           ;(let [mod-js (clj->js mod)])
                          (resolve mod))
             on-err (fn [err]
                      (error "shadow-module could not be loaded: " err)
                      (reject err))]
         (lazy/load loadable on-success on-err))))))
