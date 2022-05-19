(ns goldly.config.build.sci-bindings)

(defn make-lazy [bindings]
  (let [lazy-bindings (into {}
                            (map (fn [[k v]]
                                   [k (list 'wrap-lazy v)]) bindings))]
    lazy-bindings))

(defn make-lazy-ns [ns-bindings]
  (into {}
        (map (fn [[k v]]
               [k (make-lazy v)]) ns-bindings)))

(defn nss->requires [cljs-namespace]
  (map (fn [n]
         [n]) cljs-namespace))

; sci lazy module

(defn sci-lazy-namespace [module [sci-ns sci-ns-definition]]
  (let [sci-ns-definition
        (into {}
              (map (fn [[k v]]
                     [k ;(-> k name keyword) 
                      v])
                   sci-ns-definition))]
    [sci-ns
     {:module module
      :sci-ns-def sci-ns-definition}]))

(defn sci-lazy-namespaces [module cljs-ns-bindings]
  (let [sci-namespaces (map #(sci-lazy-namespace module %) cljs-ns-bindings)]
    ;(println "cljs-ns-bindings:" cljs-ns-bindings)
    ;(println "module:" module "sci-namespaces:" sci-namespaces)
    (into {} sci-namespaces)))

#_(->> (map (fn [the-ns-name]
              [(name the-ns-name) module-name]) ; map ns-name => module-name
            cljs-namespace)
       (into {}))

(defn extension-sci-bindings [sci-bindings
                              {:keys [lazy lazy-sci
                                      cljs-namespace
                                      cljs-bindings cljs-ns-bindings]
                               :or {cljs-namespace []
                                    cljs-bindings {}
                                    cljs-ns-bindings {}}
                               :as ext}]

  (let [module-name (:name ext)
        cljs-requires (if (or lazy lazy-sci)
                        [] ; in lazy mode namespaces cannot be required directly
                        (nss->requires cljs-namespace))
        ns-module-lazy (if lazy-sci
                         (sci-lazy-namespaces module-name cljs-ns-bindings)
                         {})
        cljs-bindings (if lazy
                        (make-lazy cljs-bindings) ; instead in lazy-mode we add the make-lazy wrapper
                        (if lazy-sci {} cljs-bindings))
        cljs-ns-bindings (if lazy
                           (make-lazy-ns cljs-ns-bindings)
                           (if lazy-sci {} cljs-ns-bindings))
        sci-lazy-ns-bindings (if lazy-sci
                               cljs-ns-bindings
                               {})]

    ;(println "ns-module-lazy module:" name "lazy: " ns-module-lazy)
    {:requires
     (concat (:requires sci-bindings) cljs-requires)
     :bindings
     (merge (:bindings sci-bindings) cljs-bindings)
     :ns-bindings
     (merge (:ns-bindings sci-bindings) cljs-ns-bindings)
     :ns-bindings-lazy
     (merge (:ns-bindings sci-bindings) sci-lazy-ns-bindings)
     :ns-module-lazy
     (merge (:ns-module-lazy sci-bindings) ns-module-lazy)}))

(defn sci-bindings-config [goldly-config exts]
  (reduce
   extension-sci-bindings
   {:requires (if (:lazy goldly-config)
                '[[webly.build.lazy :refer-macros [wrap-lazy]]]
                [])
    :bindings {}
    :ns-bindings {}
    :ns-bindings-lazy {}
    :ns-module-lazy {}}
   (vals exts)))

(comment
  (require '[goldly.config.discover :refer [discover]])
  (->> (discover {:lazy true})
       (sci-bindings-config {:lazy true}))

 ; 
  )