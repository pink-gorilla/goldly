
;; tag replacer

(defn unknown-tag
  [tag]
  [:span.bg-red-500
   (str "Unknown Tag: " tag)])

(defn replace-tag
  [lookup-fun h]
  (let [tag (first h)
        fun (lookup-fun tag)]
    (if fun
      (into [] (assoc h 0 fun))
      (unknown-tag tag))))

(defn hiccup-tree-replacer
  [h-tree fn-match-p fn-lookup]
  (walk/prewalk
   (fn [h]
     (if (fn-match-p h)
       (replace-tag fn-lookup h)
       h))
   h-tree))

;; Pinkie Tag Replacer

(defn- hiccup-vector? [h]
  (and
   (vector? h)
   (not (map-entry? h)); ignore maps
   (keyword? (first h)); reagent syntax requires first element  to be a keyword
   ))

(def pinkie-namespace (namespace :p/test))

(defn pinkie-tag? [tag]
  (let [kw-namespace (namespace tag)]
    (= pinkie-namespace kw-namespace)))

(defn p-pinkie [h]
  (if (hiccup-vector? h)
    (let [tag (first h)]
      (pinkie-tag? tag))
    false))

#_(defn pinkie-manual-lookup [t]
    (case t
      :p/clock clock))

#_(defn pinkie-registry-lookup [t]
  ; not possible to do the lookup manually, as
  ; pinkie/registry is NOT exported.
  ; (pinkie/tags) shows registered tags.
  ; pinkie/register-tag - registeres a tag.
    )
;; hiccup preprocessor

(defn ->hiccup [h-tree]
  (println "processing hiccup: " h-tree)
  [error-boundary
   (-> h-tree
       ;(hiccup-tree-replacer p-pinkie pinkie-manual-lookup)
       pinkie/render)])

