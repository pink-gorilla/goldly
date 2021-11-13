(ns goldly.extension.discover
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.edn :as edn]
   [resauce.core :as rs]
   [modular.writer]
   [modular.config :refer [config-atom]]

   ; build time
   [goldly.extension.theme :refer [add-extension-theme]]
   [goldly.extension.pinkie :refer [pinkie-atom save-pinkie]]
   [goldly.extension.cljs :refer [cljs-init add-extension-cljs]]

   ; runtime
   [goldly.extension.cljs-autoload :refer [add-extension-cljs-autoload]]
   [goldly.extension.clj :refer [add-extension-clj-require add-extension-autoload-clj-ns]]

   [goldly.extension.core :refer [save-extensions]]))

#_(defn resource-dir-paths [path]
    (let [parents (map str (rs/resources path))]
    ;(warn "parents: " (pr-str parents))
      (for [url (rs/resource-dir path)]
        (let [prefix (first (filter (partial str/starts-with? url) parents))]
          (str path (subs (str url) (count (str prefix))))))))

#_(defn resource-dir [path]
    (if (not (= path "module-info.class")) ; (rs/directory? path))
      (rs/resource-dir path)
      []))

#_(defn recursive-resource-paths [path]
    (tree-seq (comp seq resource-dir) resource-dir-paths path))

(defn add-extension [{:keys [name] :as extension}]
  (debug "adding extension: " name)
   ; build-time
  (add-extension-theme extension)
  (add-extension-cljs extension)

  ;run-time
  ;(add-extension-snippets extension)
  (add-extension-cljs-autoload extension)
  (add-extension-autoload-clj-ns extension)
  (add-extension-clj-require extension))

(defn discover []
  (let [r  (rs/resource-dir "ext")
        extensions (for [f r]
                     (-> f slurp edn/read-string))]
    (debug "discovered extensions: " (pr-str r))
    (cljs-init)
    (doall (for [ext extensions]
             (add-extension ext)))
    (save-extensions extensions)
    (save-pinkie)
    (swap! config-atom assoc :pinkie @pinkie-atom)))

(comment

  (rs/resources "demo.notebook.goldly")
  (rs/resources "")

  (-> (rs/resources "demo/notebook/apple.clj")
      first
      (rs/directory?))

  (-> (rs/resource-dir "ext")
      ;first
      last
      ;slurp
      )

  (-> (rs/resource-dir "demo/notebook")
      ;first
      last
      rs/name
      ;slurp
      )

  ;(recursive-resource-paths "ext")
  ;(recursive-resource-paths "")

;  
  )