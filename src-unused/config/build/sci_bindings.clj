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

(defn sci-lazy-module [cljs-ns-bindings]
  (->> (map (fn [[sci-ns-name sci-ns-def]]
              [sci-ns-name {:sci-def sci-ns-def
                            :loadable (vals sci-ns-def)}]) cljs-ns-bindings) ; namespaces per module is needed to find the module that needs to be loaded for a ns
       (into {})))

(defn sci-lazy-ns-dict [module cljs-ns-bindings]
  (->>  (map (fn [[sci-ns-name _sci-ns-def]]
               [sci-ns-name module]) cljs-ns-bindings)
        (into {})))

(defn extension-sci-bindings [s
                              {:keys [lazy lazy-sci
                                      cljs-namespace
                                      cljs-ns-bindings]
                               :or {cljs-namespace []
                                    cljs-ns-bindings {}}
                               :as ext}]

  (let [module-name (:name ext)]
    ;(println "ns-module-lazy module:" name "lazy: " ns-module-lazy)
    {:requires
     (concat (:requires s) (if (or lazy lazy-sci)
                             [] ; in lazy mode namespaces cannot be required directly
                             (nss->requires cljs-namespace)))
     :ns-bindings
     (merge (:ns-bindings s)  (if lazy
                                (make-lazy-ns cljs-ns-bindings)
                                (if lazy-sci {} cljs-ns-bindings)))
     :lazy-modules (if lazy-sci
                     (assoc (:lazy-modules s) module-name (sci-lazy-module cljs-ns-bindings))
                     (:lazy-modules s))

     :sci-lazy-ns-dict (if lazy-sci
                         (merge (:sci-lazy-ns-dict s) (sci-lazy-ns-dict module-name cljs-ns-bindings))
                         (:sci-lazy-ns-dict s))}))

(defn sci-bindings-config [goldly-config exts]
  (reduce
   extension-sci-bindings
   {:requires (if (:lazy goldly-config)
                '[[webly.build.lazy :refer-macros [wrap-lazy]]]
                [])
    :ns-bindings {}
    :lazy-modules {}
    :sci-lazy-ns-dict {}}
   (vals exts)))

(comment
  (require '[goldly.config.discover :refer [discover]])
  (->> (discover {:lazy true})
       (sci-bindings-config {:lazy true}))

 ; 
  )

(comment

  (defn get-deps-from-classpath []
    (let [deps
          (-> (Thread/currentThread)
              (.getContextClassLoader)
              (.getResources "goldly_bindings_generated.cljs")
              (enumeration-seq)
              (->> (map (fn [url]
                          #_(-> (slurp url)
                                (edn/read-string)
                                (select-keys [:npm-deps])
                                (assoc :url url))
                          url))
                   (into [])))]
      deps))

  (get-deps-from-classpath)
;
  )
