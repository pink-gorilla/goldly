(ns goldly.system
  "defines reagent-component render-system, that displays a fully defined system"
  (:require
   [clojure.string :as str]
   [taoensso.timbre :as timbre :refer-macros [tracef
                                              debug debugf
                                              info infof
                                              warnf
                                              errorf error]]
   [clojure.walk :as walk]
   [sci.core :as sci]
   [cljs.tools.reader :as reader]
   [cljs.reader]
   [reagent.core :as r]
   [reagent.dom]
   [re-frame.core :refer [dispatch subscribe]]
   [cljs-uuid-utils.core :as uuid]
   [com.rpl.specter :refer [transform setval END]]
   [pinkie.pinkie :as pinkie]
   ;[pinkgorilla.ui.gorilla-plot.pinkie :refer [sin]]
   ))

(defn sin [x] 0.5)

;; cljs compile


(def ^:private walk-ns {'postwalk walk/postwalk
                        'prewalk walk/prewalk
                        'keywordize-keys walk/keywordize-keys
                        'walk walk/walk
                        'postwalk-replace walk/postwalk-replace
                        'prewalk-replace walk/prewalk-replace
                        'stringify-keys walk/stringify-keys})

;(def bindings-default {'println println})


(defn compile [code bindings]
  (sci/eval-string code {:bindings bindings
                         :preset {:termination-safe true}
                         :namespaces {'walk walk-ns}}))

;; compile system

(defn- edn? [obj]
  (or (number? obj)
      (string? obj)
      (coll? obj)
      (boolean? obj)
      (nil? obj)
      (regexp? obj)
      (symbol? obj)
      (keyword? obj)))

(defn- norm-evt [obj]
  (->> obj
       js/Object.getPrototypeOf
       js/Object.getOwnPropertyNames
       (map #(let [norm (-> %
                            (str/replace #"[A-Z]" (fn [r] (str "-" (str/lower-case r))))
                            keyword)]
               [norm (aget obj %)]))
       (filter (comp edn? second))
       (into {})))

(defn- eventhandler-fn [state fun]
  (fn [e & args]
    (try
      (info "running eventhandler fn: " fun "e:" e " args: " args)
      ;(println "running eventhandler with state: " @state)
      (.preventDefault e)
      (.stopPropagation e)
      (let [e-norm (norm-evt (.-target e))
            _   (println "eventhandler e-norm: " e-norm)
            _ (println "args: " args)
            fun-args [e-norm @state]
            fun-args (if (nil? args)
                       fun-args
                       (into [] (concat fun-args args)))
            _ (println "fun-args: " fun-args)]
        (->> (apply fun fun-args)
             (reset! state))
        (println "new state: " @state))
      (catch :default e
        (.log js/console "eventhandler-fn exception: " e)))))

(defn no-op-fun [f-name]
  (fn [state e-norm]
    (println "running function " f-name " (no-fun) ..")))

(defn compile-fn
  "compiles a system/fns. 
   On compile error returns no-op-fun"
  [bindings f-name f-body]
  (let [_ (println "compile-fn " f-name " bindings: " (keys bindings) " code: " f-body)
        f-body (cljs.reader/read-string f-body)
        _ (println "fbody: " f-body)
        fun (compile f-body bindings)
        _ (println "fun: " fun)
        fun (if fun
              fun
              (do (println "compile error in system/fn " f-name)
                  (no-op-fun f-name)))]
    fun))

(defn clj-fun [run-id system-id fn-clj]
  (fn [& args]
    (infof "runner %s : system %s calling fn-clj %s" run-id system-id fn-clj)
    (let [fn-vec [run-id system-id fn-clj]
          fn-vec (if args (into fn-vec args) fn-vec)]
      (dispatch [:goldly/send :goldly/dispatch fn-vec])
      nil)))

(defn binding-symbol [f-name]
  (->> f-name name (str "?") symbol))

(defn- ->bindings-clj [run-id system-id fns-clj]
  (let [fns-clj (or fns-clj [])
        fns-keys (map binding-symbol fns-clj)
        bindings-clj  (zipmap fns-keys (map (partial clj-fun run-id system-id) fns-clj))]
    (println "bindings-clj: " bindings-clj)
    bindings-clj))

(defn- ->bindings-cljs [state fns]
  (let [bindings {'state state
                  'sin sin}
        bindings-cljs (->> fns
                           (map (fn [[f-name f-body]]
                                  [(binding-symbol f-name)
                                   (->> (compile-fn bindings f-name f-body)
                                        (eventhandler-fn state))]))
                           (into bindings))]
    (println "bindings-cljs: " bindings-cljs)
    bindings-cljs))

(defn tap [x]
  (println "tap:" x)
  x)

(defn compile-error [s b e]
  [:div.border.border-red-500.m-5.p-3
   [:h3.text-purple-700-w-full.bg-pink-300.mb-5 "Error compiling system"]
   [:h1.text-blue-300 "system"]
   [:p (pr-str s)]
   [:h1.text-blue-300 "bindings"]
   [:p (pr-str b)]
   [:h1.text-blue-300 "Error"]
   [:p (pr-str e)]])

(defn compile-system
  "compiles a system and creates a reagent component that displays the system;
   or that displays a compile error
   returns: component
            component expects system state as parameter as atom"
  [run-id state-a {:keys [id state html fns fns-clj] :as system}]
  (info "compiling system: " system)
  (if (nil? html)
    (fn [_] [:h1 "Error: system html is nil!"])
    (let [_ (println "compile-system ..")
          bindings-cljs (->bindings-cljs state-a fns)
          bindings-clj  (->bindings-clj run-id id fns-clj)
          bindings (merge bindings-clj bindings-cljs)
          _ (println "bindings-system: " bindings)
          component (fn [state-a]
                      (try
                        (-> (compile html bindings)
                            pinkie/tag-inject)
                        (catch :default e
                          (.log js/console e)
                          (fn [state-a] [compile-error {:state @state-a
                                                        :html html
                                                        :fns fns
                                                        :fns-clj fns-clj} bindings e]))))]
      component)))

(defn specter-resolve
  [specter-vector]
  (walk/prewalk
   (fn [x] (if (keyword? x)
             (case x
               :END END
               x)
             x))
   specter-vector))

(defn update-state-from-clj-result [state result where]
  (debugf "updating state from clj result: %s where: %s" result where)
  (try
   ;(com.rpl.specter/setval [:a] 1 m) set key a to 1 in m
    (let [_ (info "specter where: " where)
          where-resolved (specter-resolve where)
          _ (info "specter resolved: " where-resolved)]
      (reset! state (setval where-resolved result @state))
      (debug "update state from clj success!")
      (debug "system state after clj-update: " (pr-str state)))
    (catch :default e
      (error "exception in updating state after clj result call:" e))))

(defn render-system-impl [run-id]
  (fn [{:keys [state html fns fns-clj] :as system}]
    (if (nil? html)
      [:h1 "Error: system html is nil!"]
      (let [state-a (r/atom state)
            component (compile-system run-id state-a system)
            update-state (partial update-state-from-clj-result state-a)]
        (dispatch [:goldly/add-running-system run-id (merge system {:update-state update-state})])
        (fn []
          [component state-a])))))

; note that splitting render-system and render-system-impl has the reason
; to not recompile the code at each rerender of the data that the system
; is displaying. 

(defn render-system [{:keys [state html fns fns-clj id] :as system}]
  (let [id (uuid/uuid-string (uuid/make-random-uuid))]
    (r/create-class
     {:display-name          "goldly-render-system"
      :reagent-render        (render-system-impl id)
      :component-will-unmount (fn [this] ;  just before the component is unmounted from the DOM.
                                (infof "render-system id: %s - will unmount" id)
                                (dispatch [:goldly/remove-running-system id]))})))



