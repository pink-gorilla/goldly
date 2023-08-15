(ns goldly.sci.loader.shadow-module
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core]
   [sci.core :as sci]
   [sci.async :as scia]
   [goldly.sci.loader.load-shadow :refer [load-ext-shadow]]
   [goldly-bindings-generated :refer [sci-lazy-ns-dict lazy-modules]]
   [goldly.sci.loader.static :refer [dynamic-base]]))

(defn patch-url [uris uri-name]
  (let [base (dynamic-base)
        uri (aget uris uri-name)
        ;_ (.log js/console "uri: " uri)
        l (.-length uri)]
    ;(.log js/console "length: " l)
    (doall (for [i (range l)]
             (do
        ;(.log js/console "setting index: " i)
               (let [el (aget uri i)] ; "/r/tick.js"
          ;(.log js/console "el: " el)
                 (aset uri i (str base el))))))))

(defn set-shadow-load-dir [url]
  ; goog.global.SHADOW_ENV.scriptBase
  ; "http://localhost:8080/r/cljs-runtime/"
  (set! (.. js/window -goog -global -SHADOW_ENV -scriptBase)
        url)
  (let [;Object.keys (goog.global.shadow$modules["uris"])
        shadow-modules (.. js/window -goog -global -shadow$modules)
        uris (aget shadow-modules "uris")
        uri-names (.keys js/Object uris)]
    ; ['tick', 'ui-binaryclock', 'ui-container',
    ; 'ui-video', 'ui-spark', 'ui-codemirror', 'ui-repl']
    (doall (map (partial patch-url uris) uri-names))))

(defn dynamic-cljs-runtime-dir []
  (let [base (dynamic-base)]
    (str base "/r/cljs-runtime/")))

(defn set-github-load-mode []
   ;  https://awb99.github.io/walhalla/r/cljs-runtime/
  (let [dir (dynamic-cljs-runtime-dir)]
    (info "github shadow base: " dir)
    (set-shadow-load-dir dir)))

(defn sci-ns-lookup [libname]
  ; (str libname)
  ;(debug "available lazy namespaces:" (pr-str sci-lazy-ns-dict))
  (debug "looking up module for sci-ns:" libname)
  (if-let [module-name (get sci-lazy-ns-dict libname)]
    (do (info "module for " libname ": " module-name)
        (get lazy-modules module-name))
    (do (info "no lazy-module found for: " libname)
        nil)))

(defn add-sci-ns [ctx libname ns opts sci-ns sci-defs ns-vars]
  (info "creating sci ns: " sci-ns "ns-vars:" ns-vars "sci-defs" sci-defs)
  (let [mlns (sci/create-ns sci-ns)
        sci-ns-def (->> (map (fn [sci-def ns-var]
                               ;(info "ci-def:" sci-def "ns-var:" ns-var)
                               ;(when-let [joke (:joke mod)]
                               ;  (info "joke: " (joke)))
                               (when (= sci-def :add)
                                 (info "TEST: adding: " (ns-var 7 7)))

                               [sci-def (sci/new-var sci-def ns-var {:ns mlns})])
                             sci-defs ns-vars)
                        (into {}))]
    (info "sci/add-namespace! sci-ns: " libname " sci ns :" sci-ns "def: " sci-ns-def)
    (sci/add-namespace! ctx libname sci-ns-def)))

(defn load-module-ns [ctx libname ns opts sci-ns sci-def loadable]
  (-> (load-ext-shadow loadable)
      (.then
       (fn [ns-vars]
         (info "received ns-vars for sci-ns: " sci-ns "libname: " libname "ns: " ns)
         (add-sci-ns ctx libname ns opts sci-ns sci-def ns-vars)))))

(defn load-module [ctx libname ns opts sci-mod]
  (info "load-shadow-module: ns: " libname)
  (let [promises (map (fn [[sci-ns {:keys [sci-def loadable]}]]
                        (load-module-ns ctx libname ns opts sci-ns sci-def loadable))
                      sci-mod)
        p-all (promesa.core/all promises) ; Given an array of promises, return a promise that is fulfilled when all the items in the array are fulfilled.
        ]
    (.then p-all
           (fn [_d]
             (info "load-shadow-module: ns: " libname " - finished!")
             ;(info "all data: " d)
             ;; empty map return value, SCI will still process `:as` and `:refer`
             {}))))

