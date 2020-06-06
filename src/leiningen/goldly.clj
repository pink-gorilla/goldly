(ns leiningen.goldly
  (:require
   [leiningen.core.eval :as eval]))

;; The version of Goldly that we will use
(def goldly-version "0.0.1")

(def default-settings
  {:port 8000})


;; This is the leiningen task. It needs no arguments, and can run outside a project 
;; (assuming you've got the plugin installed in your profile).


(defn goldly
  [project & opts]
  (let [user-settings (or (:goldly project) {})
        ;; inject the goldly dependency into the target project
        curr-deps (or (:dependencies project) [])
        new-deps (conj curr-deps ['org.pinkgorilla/goldly goldly-version])
        prj (assoc project :dependencies new-deps)]
    (eval/eval-in-project
     prj
     `((goldly.app/goldly-run! (merge default-settings user-settings)))

     '(require 'goldly.app))))



