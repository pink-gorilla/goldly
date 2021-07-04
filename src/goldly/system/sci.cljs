(ns goldly.system.sci
  "defines reagent-component render-system, that displays a fully defined system"
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]
   [reagent.core :as r]
   [reagent.dom]
   [re-frame.core :refer [dispatch]]
   [cljs-uuid-utils.core :as uuid]
   [sci.core :as sci]
  ;[cljs.tools.reader :as reader]
   [cljs.reader]
   [pinkie.pinkie :as pinkie]
   [goldly.system.eventhandler :refer [eventhandler-fn]]
   [goldly.service.result :refer [update-atom-where]]
   [goldly.sci.compile :refer [compile-code compile-fn compile-fn-raw]]
   [goldly.sci.kernel-cljs :as kernel]))

;; compile system

(defn binding-symbol [f-name]
  (->> f-name name (str "?") symbol))

(defn- ->bindings-cljs [ctx fns state-a]
  (let [bindings-cljs (->> fns
                           (map (fn [[f-name f-body]]
                                  [(binding-symbol f-name)
                                   (->> (compile-fn ctx f-name f-body)
                                        (eventhandler-fn state-a))]))
                           (into {}))]
    (debug "bindings-cljs: " bindings-cljs)
    bindings-cljs))

(defn- ->bindings-cljs-raw [ctx fns]
  (let [bindings-cljs (->> fns
                           (map (fn [[f-name f-body]]
                                  [(binding-symbol f-name)
                                   (compile-fn-raw ctx f-name f-body)]))
                           (into {}))]
    (debug "bindings-cljs-raw: " bindings-cljs)
    bindings-cljs))

(defn tap [x]
  (info "tap:" x)
  x)

(defn compile-error [s ctx e]
  [:div.border.border-red-500.m-5.p-3
   [:h3.text-purple-700-w-full.bg-pink-300.mb-5 "Error compiling system"]
   [:h1.text-blue-300 "system"]
   [:p (pr-str s)]
   ;[:h1.text-blue-300 "ctx"]
   ;[:p (pr-str ctx)]
   [:h1.text-blue-300 "Error"]
   [:p (pr-str e)]])

(defn compile-system
  "compiles a system and creates a reagent component that displays the system;
   or that displays a compile error
   returns: component
            component expects system state as parameter as atom"
  [run-id state-a ext {:keys [id state html fns fns-raw] :as system}]
  (info "compiling system: " (:id system))
  (if (nil? html)
    (fn [_] [:h1 "Error: system html is nil!"])
    (let [bindings-system {'state state-a
                           'ext ext}
          ctx (sci/fork kernel/ctx-repl)
          ctx (sci/merge-opts ctx {:bindings bindings-system})
          bindings-cljs (->bindings-cljs ctx fns state-a)
          bindings-cljs-raw (->bindings-cljs-raw ctx fns-raw)
          ctx (sci/merge-opts ctx {:bindings (merge bindings-cljs bindings-cljs-raw)})
          ;_ (debug "bindings-system: " bindings)
          component (fn [state-a]
                      (try
                        (-> (compile-code html ctx)
                            pinkie/tag-inject)
                        (catch :default e
                          (error "compile system error ex: " e)
                          (fn [state-a] [compile-error {:state @state-a
                                                        :html html
                                                        :fns fns} ctx e]))))]
      component)))

(defonce run-state (r/atom {}))

(defn make-component [run-id ext {:keys [state html fns] :as system}]
  (if (nil? html)
    nil
    (let [state-a (r/atom state)
          component (compile-system run-id state-a ext system)
          update-state (fn [result where]
                         (update-atom-where state-a where result))]
      (swap! run-state assoc run-id state-a)
      (dispatch [:goldly/add-running-system run-id (merge system {:update-state update-state})])
      (fn []
        [component state-a]))))

; note that splitting render-system and render-system-impl has the reason
; to not recompile the code at each rerender of the data that the system
; is displaying. 

(defn render-system [system ext]
  (let [run-id (uuid/uuid-string (uuid/make-random-uuid))
        compiled-system (r/atom nil)
        component (r/atom nil)
        make! (fn [system ext]
                (let [new-c [ext system]]
                  (when-not (= new-c @compiled-system)
                    (reset! component (make-component run-id ext system))
                    (reset! compiled-system new-c))))]

    (r/create-class
     {:display-name "goldly-render-system"

      :reagent-render
      (fn [system] ;; remember to repeat parameters
        (if @component
          [@component]
          [:h1 "compiling.."]))

      :component-did-mount
      (fn [this] ; oldprops oldstate snapshot
        (info "c-d-m: " (:id system))
        (make! system ext))

      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (r/argv this))
              [system ext] new-argv
                                   ;{:keys [f data]} arg1
              ]
          ;(println "c-did-update: " new-argv)
          ;(println "c-did-update:argv " (r/argv this))
          ;(println "c-did-update:argv-old " old-argv)
          (info "c-did-update: system " (:id system) "ext:" ext)
          (make! system ext)))

      :component-will-unmount
      (fn [this] ;  just before the component is unmounted from the DOM.
        (infof "render-system id: %s - will unmount" run-id)
        (dispatch [:goldly/remove-running-system run-id]))})))


