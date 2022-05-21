(ns goldly.sci.bindings
  (:require
   [clojure.core]
   [clojure.string :as str]
   [fipp.clojure]
   [modular.date :refer [now-str]]))

; generate forms

(defmacro generate-require [namespaces]
  `(concat (list :require) ~namespaces))

(defn sci-ns-vars-only-kw [[ns-name ns-def]]
   ;{'funny {:joke demo.funny/joke
   ;         :facts demo.funny/facts
   ;         :data demo.funny/data}
   ; 'animals {:all demo.animals/all}}
  (map (fn [[var impl]]
         var) ns-def))

(defn convert-module-def [module-def]  ; {'funny {:sci-def [] :loadable []} 'animals {}}
  (into {}
        (map (fn [[sci-ns {:keys [loadable sci-def]}]]
               [sci-ns {:sci-def (-> sci-def keys vec)
                        :loadable (list 'shadow.lazy/loadable (vec loadable))}])
             module-def)))

(defn convert-module [[module-name module-def]] ; "funny" {'funny {} 'animals {}}
  [module-name (convert-module-def module-def)])

(defn make-sci-lazy-ns-bindings [lazy-modules]
  ; {"adder" {'adder {:sci-def {:add adder/add,
  ;                            :mult adder/mult},
  ;                  :loadable (adder/add
  ;                             adder/mult)}},
  ; "funny" {'funny {:sci-def {:joke demo.funny/joke,
  ;                            :facts demo.funny/facts,
  ;                            :data demo.funny/data},
  ;                  :loadable (demo.funny/joke
  ;                             demo.funny/facts
  ;                             demo.funny/data)},
  ;          'animals {:sci-def {:all demo.animals/all},
  ;                    :loadable (demo.animals/all)}}}
  ;
  ; we really just want to convert :loadable here. great usecase for specter.
  ; but we dont want to use specter to bring in a new dependency.
  (->> (map convert-module lazy-modules)
       (into {})))

(defn make-forms [{:keys [requires bindings ns-bindings
                          lazy-modules sci-lazy-ns-dict]}]
  (let [nsl '(ns goldly-bindings-generated)
        r (generate-require requires)
        rl (list r)
        nslr (concat nsl rl)
        lazy-lookup (make-sci-lazy-ns-bindings lazy-modules)]
    [nslr
     (list 'def 'compile-time (now-str))
     (list 'def 'bindings-generated bindings)
     (list 'def 'ns-generated ns-bindings)
     (list 'def 'lazy-modules lazy-lookup)
     (list 'def 'sci-lazy-ns-dict sci-lazy-ns-dict)]))

; write forms to cljs 

(defn form->str [f]
  (with-out-str
    (fipp.clojure/pprint f {:width 120})))

(defn write-forms [filename forms]
  (let [comment (str "; generated by goldly on " (now-str) "\r\n")
        s-forms (-> (map form->str forms)
                    (str/join))
        ;s-compile-time (str "\r\n(def binding-compile-time " (now-str) ") \r\n") 
        s (str comment  s-forms)]
    (spit filename s)))

;; all together

(defn write-sci-binding-cljs-file [sci-config filename]
  (let [forms (make-forms sci-config)]
    (write-forms filename forms)))

(comment

  ;config management  
  ; PRODUCE LINT ERRORS
  ;(add-cljs-namespace [clojure.walk :as walk])
  ;(add-cljs-namespace [goldly.sci.bindings-goldly])
  ;(add-cljs-namespace [goldly.code.core])
  ;(add-cljs-bindings {'sin sin
  ;                    'println println})

  ; generate forms

  (def static-forms
    ['(ns goldly.sci.bindings-generated
        (:require
         [clojure.walk :as walk]
         [goldly.sci.bindings-goldly :refer [sin]]))
     '(def bindings-generated
        {'sin sin
         'println println})])

  (make-forms)

  (sci-bindings-filename)
  ; all together
  (generate-bindings)
  ;
  )