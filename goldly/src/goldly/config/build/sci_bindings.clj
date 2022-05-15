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

(defn extension-sci-bindings [sci-bindings
                              {:keys [lazy
                                      cljs-namespace
                                      cljs-bindings cljs-ns-bindings]
                               :or {cljs-namespace []
                                    cljs-bindings {}
                                    cljs-ns-bindings {}}
                               :as ext}]

  (let [cljs-requires (if lazy
                        [] ; in lazy mode namespaces cannot be required directly
                        (nss->requires cljs-namespace))
        cljs-bindings (if lazy
                        (make-lazy cljs-bindings) ; instead in lazy-mode we add the make-lazy wrapper
                        cljs-bindings)
        cljs-ns-bindings (if lazy
                           (make-lazy-ns cljs-ns-bindings)
                           cljs-ns-bindings)]
    {:requires
     (concat (:requires sci-bindings) cljs-requires)
     :bindings
     (merge (:bindings sci-bindings) cljs-bindings)
     :ns-bindings
     (merge (:ns-bindings sci-bindings) cljs-ns-bindings)}))

(defn sci-bindings-config [goldly-config exts]
  (reduce
   extension-sci-bindings
   {:requires (if (:lazy goldly-config)
                '[[webly.build.lazy :refer-macros [wrap-lazy]]]
                [])
    :bindings {}
    :ns-bindings {}}
   (vals exts)))

(comment
  (require '[goldly.config.discover :refer [discover]])
  (->> (discover {:lazy true})
       (sci-bindings-config {:lazy true}))

 ; 
  )