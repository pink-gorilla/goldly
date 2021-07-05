(ns systems.snippet-runner
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [clojure.core :refer [read-string load-string]]
   [goldly.system.db :refer [add-system]]
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]])
  (:import [goldly.runner GoldlySystem]))

; pinkie

(defn html->system [id src]
  {:id id
   :state {}
   :html (read-string src)
   :fns {}})

(defn start-pinkie [{:keys [src id] :as snippet}]
  (system-start!
   (goldly/system-html
    (html->system id src)
    {:fns {}})))

; goldly

(defn goldly->system [id src]
  (let [s (read-string src)]
    (assoc s :id id)))

(defn start-goldly [{:keys [src id] :as snippet}]
  (system-start!
   (goldly/system-html
    (goldly->system id src)
    {:fns {}})))

; goldly-cli

(defn start-goldly-clj [{:keys [src id] :as snippet}]
  (let [;s (read-string src)
        ;x (eval s)
        x (load-string src)]
    snippet))

(defn start-snippet [{:keys [type id] :as s}]
  (with-redefs [goldly.runner/system-start!
                (fn [system]
                  (let [system (assoc system :id id)]
                    (info "starting system " id)
                    (add-system system)
                    (GoldlySystem. id)))]
    (case type
      :pinkie (start-pinkie s)
      :goldly (start-goldly s)
      :goldly-clj (start-goldly-clj s)
      (error "cannot start snippet of unknown type: " s))))