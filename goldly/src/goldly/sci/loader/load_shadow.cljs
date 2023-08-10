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
  (js/Promise.
   (fn [resolve reject]
     (let [handle-load (fn [mod]
                         (info "shadow module-ns did load: " mod)
                         ;(let [mod-js (clj->js mod)])
                         (resolve mod))]
       (lazy/load loadable handle-load)))))
