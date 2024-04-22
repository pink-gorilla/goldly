(ns demo.notebook.ajax
  (:require
   [clojure.edn :as edn]
   [promesa.core :as p]
   [ajax.promise :refer [GET]]))

(defn json-get [url]
  (p/let [resp (js/fetch url)
          json (.json resp)]
    (js->clj json :keywordize-keys true)))

(defn edn-get [url]
  (p/let [resp (GET url)
          edn (edn/read-string resp)]
    edn))

(json-get "/r/repl/bongo.json")

(edn-get "/r/repl/bongo.edn")

(def r (edn-get "/r/repl/bongo.edn"))

(type r)
(p/promise? r)

; not in sci-configs - ticket made.
(p/pending? r)
(p/resolved? r)
(p/done? r)
(p/extract r)
(deref r)
(println @r)