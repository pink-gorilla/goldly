(ns goldly.system
  (:require [clojure.walk]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

(defmacro def-ui [symbol form]
  `(do
     (def ~symbol
       (with-meta ~form {:goldly.ui true}))))

(defn goldly? [s]
  (contains? (meta s) :goldly.ui))

(defn goldly-def->val [s]
  ;(println "rep:" s)
  (if (symbol? s) ; s is (possibly) an UNRESOLVED symbol.
    (if-let [r (resolve s)] ; checks if variable is has been defined
      (let [v @r]
        ;(println "........... esc: symbol: " s  " resolve: " r  "val: " v)
        (if (goldly? v) ;(fn? v)
          v
          s ;(name i)
          ))s)
    s))

(defmacro escape-html [f]
  (pr-str
   (clojure.walk/prewalk goldly-def->val f)))

(defn escape-html2 [f]
  (pr-str
   (clojure.walk/prewalk goldly-def->val f)))

(comment
  (def no "so-bad")
  (def-ui y [:p 67])
  (goldly? y)
  (goldly? no)
  (escape-html [+ no 8 8 y (println "hello" 42)])
  (macroexpand '(escape-html [+ 8 8 y]))

  (escape-html
   {:name "click counter"
    :state 42
    :html  [:div "Clicked "
            [:p/button {:on-click ?incr} @state]
            " times"]
    :fns {:y y
          :incr (fn [s] (inc s))}})

  ;  
  )

(defn into-mapper
  "applies function f on all values of a map.
   returns a map with the same keys"
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defmacro system [{:keys [id hidden state html fns fns-raw] :as system-cljs} & system-clj]
  (let [fns (zipmap (keys fns)
                    (map #(pr-str %) (vals fns)))]
    {:id id ;(unique-id)
     :hidden hidden
     :cljs {:state state
            :html (escape-html2 html) ;(pr-str html)
            :fns (into-mapper pr-str fns)
            :fns-raw (into-mapper pr-str fns-raw)}}))

(defn system-html [{:keys [id hidden state html fns] :as system-cljs} system-clj]
  (let [fns (zipmap (keys fns)
                    (map #(pr-str %) (vals fns)))]
    {:id id
     :hidden hidden
     :cljs {:state state
            :html (escape-html2 html) ;(pr-str html)
            :fns (into-mapper pr-str fns)}
     :clj system-clj}))

(comment

  (def y (defn add [a b] (+ a b)))
  (def-ui y [:p "wow"])
  (macroexpand (system {:html [:h1 [y]]
                        :fns {:a 6
                              :b y
                              :c "g"}
                        :state 9} 2))
  ;
  )

(defn system->cljs
  "converts a system from clj to cljs"
  [system]
  (let [clj (or (get-in system [:clj :fns]) {})
        system-cljs (-> system
                        (dissoc :clj)
                        (assoc :fns-clj (into [] (keys clj))))]
    ;(println "system-cljs: " system-cljs)
    system-cljs))

(defn function?
  "Returns true if argument is a function or a symbol that resolves to
  a function (not a macro)."
  {:added "1.1"}
  [x]
  (if (symbol? x)
    (when-let [v (resolve x)]
      (when-let [value (var-get v)]
        (and (fn? value)
             (not (:macro (meta v))))))
    (fn? x)))

(defn pr-fn [name form]
  (let [m (meta form)]
    (if (contains? m :goldly.ui)
      (pr-str form)
      name)))

(defmacro resolve-ui-fn [form]
  (if (symbol? form)
    `(pr-fn ~(str form) ~form)
    (pr-str form)))

(defmacro resolve-ui-fn2 [form]
  (if (symbol? form)
    `(pr-fn ~(str form) ~form)
    (pr-str form)))

(comment
  ;(def-ui x 42)
  (def-ui y  [:p/a 5 6])
  (def-ui z '(fn [a b] (+ a b)))
  (resolve-ui-fn y)
  (resolve-ui-fn z)
  (resolve-ui-fn (fn [b c] (+ b c)))
  (resolve-ui-fn println)
  ;
  )
