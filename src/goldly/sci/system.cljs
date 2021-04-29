(ns goldly.sci.system
  "defines reagent-component render-system, that displays a fully defined system"
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [reagent.core :as r]
   [reagent.dom]
   [re-frame.core :refer [dispatch]]
   [cljs-uuid-utils.core :as uuid]
   [clojure.walk :as walk]
 ;[cljs.tools.reader :as reader]
   [cljs.reader]
   [sci.core :as sci]
   [pinkie.pinkie :as pinkie]
   [goldly.runner.eventhandler :refer [eventhandler-fn]]
   [goldly.runner.clj-fn :refer [clj-fun update-state-from-clj-result]]
   [goldly.sci.bindings-default :refer [bindings-default]]))


;; https://github.com/borkdude/sci
;; ClojureScript, even when compiled with :advanced, and (as a consequence) JavaScript

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


(defn no-op-fun [f-name]
  (fn [state e-norm]
    (info "running function " f-name " (no-fun) ..")))

(defn compile-fn
  "compiles a system/fns. 
   On compile error returns no-op-fun"
  [bindings f-name f-body]
  (let [_ (info "compile-fn " f-name " bindings: " (keys bindings) " code: " f-body)
        f-body (cljs.reader/read-string f-body)
        _ (info "fbody: " f-body)
        fun (compile f-body bindings)
        _ (trace "fun: " fun)
        fun (if fun
              fun
              (do (info "compile error in system/fn " f-name)
                  (no-op-fun f-name)))]
    fun))

(defn binding-symbol [f-name]
  (->> f-name name (str "?") symbol))

(defn- ->bindings-clj [run-id system-id fns-clj]
  (let [fns-clj (or fns-clj [])
        fns-keys (map binding-symbol fns-clj)
        bindings-clj  (zipmap fns-keys (map (partial clj-fun run-id system-id) fns-clj))]
    (debug "bindings-clj: " bindings-clj)
    bindings-clj))

(defn- ->bindings-cljs [state fns]
  (let [bindings {'state state}
        bindings-cljs (->> fns
                           (map (fn [[f-name f-body]]
                                  [(binding-symbol f-name)
                                   (->> (compile-fn bindings f-name f-body)
                                        (eventhandler-fn state))]))
                           (into bindings))]
    (debug "bindings-cljs: " bindings-cljs)
    bindings-cljs))

(defn tap [x]
  (info "tap:" x)
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
    (let [bindings-cljs (->bindings-cljs state-a fns)
          bindings-clj  (->bindings-clj run-id id fns-clj)
          bindings (merge bindings-default bindings-clj bindings-cljs)
          _ (debug "bindings-system: " bindings)
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



