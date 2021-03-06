(ns goldly.sci.lazy
  (:require
   [cljs.core.async :refer [>! <! chan close! put! take!] :refer-macros [go]]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [reagent.core :as r]
   [shadow.lazy :as lazy]
   [goldly.service.core :refer [run]]))

; https://code.thheller.com/blog/shadow-cljs/2019/03/03/code-splitting-clojurescript.html
; https://clojureverse.org/t/shadow-lazy-convenience-wrapper-for-shadow-loader-cljs-loader/3841

; (def xy (lazy/loadable [demo.thing/x demo.other/y]))
; (def xym (lazy/loadable {:x demo.thing/x
;                         :y demo.other/y}))
; (def x (lazy/loadable snippets.snip/add))

(defn load-extension [name]
  (info "loading extension: " name)
  (go
    (let [{:keys [error result] :as r} (<! (run {:fun :extension/load :args [name]}))]
      (when error
        (warn "error loading extension: " r))
      (when result
        (info "extension: " result)
        r))))

(def ext-snippets
  ;{:cljs-bindings {'add snippets.snip/add}}
  (lazy/loadable {:add snippets.snip/add}))

(defn lazytest []
  (let [on-load (fn [r]
                  (let [add (:add r)]
                    (info "add: " (add 7 7))))]
    (lazy/load ext-snippets on-load)))

(defn timestamp []
  (.getTime (js/Date.)))

(defn block [a ms]
  (let [timeout  (+ (timestamp) ms)]
    (loop [t (timestamp)]
      ;(info "time:" t "timeout: " timeout)
      (when (not @a)
        (if (< t timeout)
          (recur (timestamp))
          (error "block has timed out."))))))

(defn load-ext-shadow [ext]
  (info "load-ext-shadow .. ")
  (let [a (r/atom nil)
        handle-load (fn [r]
                      (error "loaded: " r)
                      (reset! a r))]
    (lazy/load ext handle-load)
    (block a 5000)
    (warn "lazyly loaded: " @a)
    @a))

(defn load-fn [{:keys [namespace] :as p}]
  (error "lazy load-fn: " namespace)
  (when (= namespace 'snippets)
    (let [a (load-ext-shadow ext-snippets)]
      (error "atom: " a)
      {:file "snippets.clj"
       :source "(ns user) (def add :foo)"})))

; tis does not work. block times out before shadow load completes.
#_(defn lazytest2 []
    (let [add (-> (load-fn {:namespace 'snippets})
                  (:add))]
      (add 7 7)))
