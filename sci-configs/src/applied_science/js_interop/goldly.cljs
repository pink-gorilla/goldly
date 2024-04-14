(ns applied-science.js-interop.goldly
  (:require
   [applied-science.js-interop :as j]
   [goog]
   [goog.object :as g]))

(defn value-of
  "Safe access to a value at key a js object.
   Returns `'forbidden` if reading the property would result in a `SecurityError`.
   https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy"
  [obj k]
  (try
    (let [v (j/get obj k)]
      (.-constructor v) ;; test for SecurityError
      v)
    (catch js/Error ^js _
      'forbidden)))

; ((value-of window 'y) 33)   (if y is a function)

(defn obj->clj [x]
  (-> (fn [result key]
        (let [v (aget x key)]
          (if (= "function" (goog/typeOf v))
            result
            (assoc result key v))))
      (reduce {} (goog.object/getKeys x))))