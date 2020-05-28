(ns shiny.core
  (:require
   [clojure.string :as str]
   [clojure.walk :as walk]
   [sci.core :as sci]
   [cljs.tools.reader :as reader]
   [reagent.core :as r]
   [reagent.dom]
   [pinkgorilla.ui.pinkie :as pinkie]
   [pinkgorilla.ui.default-setup]
   [pinkgorilla.ui.default-renderer] ; add ui renderer definitions 
   ))

;; cljs compile

(def ^:private walk-ns {'postwalk walk/postwalk
                        'prewalk walk/prewalk
                        'keywordize-keys walk/keywordize-keys
                        'walk walk/walk
                        'postwalk-replace walk/postwalk-replace
                        'prewalk-replace walk/prewalk-replace
                        'stringify-keys walk/stringify-keys})

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
      (println "running eventhandler with state: " @state)
      (println "eventhandler fn: " fun)
      (println "eventhandler e: " e)
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

(defn- ->bindings [state fns]
  (let [bindings {'state state}]
    (->> fns
         (map (fn [[f-name f-body]]
                [(->> f-name name (str "?") symbol)
                 (->> (compile-fn bindings f-name f-body)
                      (eventhandler-fn state))]))
         (into bindings))))

(defn tap [x]
  (println "tap:" x)
  x)

(defn compile-system [state-a html fns]
  (let [_ (println "compiling system/binding-fns ..")
        bindings (->bindings state-a fns)
        _ (println "compiling system/binding-fns success!")
        _ (println "bindings: " (keys bindings))
        system (fn [state]
                 (try
                   (-> (compile html bindings)
                       pinkie/tag-inject)
                   (catch :default e
                     (.log js/console e)
                     [:div.error "Error compiling system/htm: " (pr-str e)])))
        _ (println "system: " system)]
    system))

(defn render-system [{:keys [state html fns]}]
  (let [state-a (r/atom state)
        system (compile-system state-a html fns)]
    (fn []
      [system state-a])))
