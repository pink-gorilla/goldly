(ns goldly.extension.discover
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [fipp.clojure]
   [clojure.edn :as edn]
   [resauce.core :as rs]
   [webly.writer]
   [webly.config :refer [config-atom]]
   [goldly.extension.theme :refer [add-extension-theme]]
   [goldly.extension.snippets :refer [add-extension-snippets]]
   [goldly.extension.sci :refer [add-extension-sci]]
   [goldly.extension.clj :refer [add-extension-clj]]
   [goldly.extension.pinkie :refer [add-extension-pinkie pinkie-atom]]))

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
  (info "adding extension: " name)

  (add-extension-theme extension)
  (add-extension-sci extension)
  (add-extension-snippets extension)
  (add-extension-clj extension)
  (add-extension-pinkie extension))

(defn pr-str-fipp [config]
  (with-out-str
    (fipp.clojure/pprint config {:width 40})))

(defn save-extensions [extensions]
  (webly.writer/ensure-directory-webly)
  (->> (pr-str-fipp extensions)
       (spit ".webly/extensions.edn")))

(defn save-pinkie []
  (webly.writer/ensure-directory-webly)
  (->> (pr-str-fipp @pinkie-atom)
       (spit ".webly/pinkie.edn")))

(defn discover []
  (let [r  (rs/resource-dir "ext")
        extensions (for [f r]
                     (-> f slurp edn/read-string))]
    (debug "discovered extensions: " (pr-str r))
    (save-extensions extensions)
    (doall (for [ext extensions]
             (add-extension ext)))
    (save-pinkie)
    (error "pinkie config: " @pinkie-atom)
    (swap! config-atom assoc :pinkie @pinkie-atom)))

(comment

  (rs/resources "ext")
  (rs/resources "")

  (-> (rs/resource-dir "ext")
      ;first
      last
      ;slurp
      )
  (recursive-resource-paths "ext")
  (recursive-resource-paths "")

  (map str [])
  ;  
  )