(ns goldly.sci.kernel-cljs
  (:require
   [goog.object :as g]
   [taoensso.timbre :as timbre :refer-macros [debugf info error]]
   ; sci
   [sci.core :as sci]
   [sci.async :as scia]
   [sci.impl.resolve :as sci-resolve]
   ; bindings
   ;[goldly.sci.bindings-static :refer [ns-static]]
   [goldly-bindings-generated :refer [bindings-generated ns-generated lazy-lookup]]
   ;[goldly.sci.lazy :refer [load-fn]]
   [goldly.sci.load-shadow :refer [load-ext-shadow]]
   [shadow.lazy :as lazy])
  #_(:require-macros
     [goldly.app.build :refer [sci-lazy-registry]]))

;(defn add-lazy [namespaces]
;  (assoc namespaces
;         'snippets
;         {'add (sci/new-var 'add :internal)}))

(declare ctx-repl) ; since we want to add compile-sci to the bindings, we have to declare the ctx later

; from scittle
;(defn ^:export eval-string [s]
;  (try (sci/eval-string* @ctx s)
;       (catch :default e
;         (error/error-handler e (:src @ctx))
;         (let [sci-error? (isa? (:type (ex-data e)) :sci/error)]
;           (throw (if sci-error?
;                    (or (ex-cause e) e)
;                    e))))))

(defn ^:export compile-code [code]
  (try
    {:result (sci/eval-string* ctx-repl code)
        ;(sci/eval-string code ctx-repl)
     }
    (catch :default e
      (timbre/error "sci compile-code --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(defn resolve-symbol [sym]
  (sci-resolve/resolve-symbol ctx-repl sym))

;(def lazy-sci-dict (sci-lazy-registry))

(defn sci-ns-lookup [libname]
  ; (str libname)
  (println "available lazy namespaces:" (pr-str lazy-lookup))
  (println "looking up sci-ns:" libname)
  ;{'fun {:module "fun", :loadable (shadow.lazy/loadable {'joke demo.funny/joke})},
  ; 'adder {:module "adder", :loadable (shadow.lazy/loadable {'add adder/add})}}
  (get lazy-lookup libname)
  ;(case mod
  ;  "adder" (lazy/loadable {:add adder/add})
  ;  "fun" (lazy/loadable {:joke demo.funny/joke})
  ;  )
  )

(defn async-load-fn
  [{:keys [libname opts ctx ns]}]
  (println "async-load: " libname)
  (let [sci-mod (sci-ns-lookup libname)]
    (cond
      (= libname "some_js_lib")
      (-> (js/Promise.resolve #js {:libfn (fn [] "result!")})
          (.then (fn [mod]
                   (error "demo lib : " libname "did load: " mod "mod-clj:" (js->clj mod))
                   (sci/add-class! ctx (:as opts) mod)
                   (sci/add-import! ctx ns (:as opts) (:as opts))
                   {:handled true})))
      sci-mod
      (let [{:keys [module loadable]} sci-mod]
        (-> (load-ext-shadow module loadable)
            (.then (fn [mod]
                     (error "shadow lib : " libname "did load: " mod "mod-clj:" (js->clj mod))
                     (sci/add-class! ctx (:as opts) mod)
                     (sci/add-import! ctx ns (:as opts) (:as opts))
                     {:handled true})))))))

(defn ^:export compile-code-async [code]
  (try
    {:result (scia/eval-string* ctx-repl code)}
    (catch :default e
      (timbre/error "sci compile-code-async --]" code "[-- ex: " e)
      {:error  {:root-ex (.-data e)
                :err (.-message e)}})))

(def ctx-static
  {:bindings (assoc bindings-generated
                    'compile-sci compile-code
                    'compile-sci-async compile-code-async
                    'resolve-symbol-sci resolve-symbol)
   :preset {:termination-safe false} ; was: true
   :namespaces ns-generated ; (add-lazy ns-generated) ; ns-static

   :classes  {'js js/window :allow :all}
   ;:classes  {'js goog/global :allow :all} ; In JS hosts, to allow interop with anything, use the following config:
   ;:classes {'js js/goog.global
             ;:allow :all
            ; 'js goog.global ; this returns the same as window.
            ; 'console js/console
            ; 'String js/String
             ;'js2 js/window
             ;'window js/window
    ;         }
   :disable-arity-checks true ; from clerk
   :async-load-fn async-load-fn
   ;:load-fn load-fn
   })

(def ctx-repl (sci/init ctx-static))

#_(defmethod kernel-eval :cljs [{:keys [id code]
                                 :or {id (guuid)}}]
    (let [c (chan)]
      (info "sci-eval: " code)
      (go (try (let [{:keys [error result]} (compile-code code)
                     eval-result (if error
                                   (merge {:id id} error)
                                   {:id id :picasso (->picasso result)})]
                 (>! c eval-result))
               (catch js/Error  e
                 (error "eval ex: " e)
                 (>! c {:id id
                        :error e})))
          (close! c))
      c))
