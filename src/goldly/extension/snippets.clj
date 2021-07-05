(ns goldly.extension.snippets
  (:require
   [systems.snippet-registry :refer [add-snippet]]))

(defn add-extension-snippets [{:keys [name snippets]
                               :or {snippets []}
                               :as extension}]
  (doall (for [s snippets]
           (add-snippet s))))