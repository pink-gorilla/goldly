(ns ui.goldly.fun
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info warn error]]
   [re-frame.core :as rf]))

; why is this here?

; this is a clojurescript namespace
; this functions have to go to the bundel
; goldly has a system to add code to bundels, and this needs to be tested.


(defn nav [& args]
  (rf/dispatch (into [] (concat [:bidi/goto] args))))

(defn set-system-state [system-id result where]
  (rf/dispatch [:goldly/set-system-state {:system-id system-id
                                          :result result
                                          :where where}]))

(defn timeout [f ms]
  (js/setTimeout f ms))

(defn evt-val [e]
  (.. e -target -value))

(defonce clipboard (atom nil))

(defn clipboard-set [val]
  (info "clipboard-set: " val)
  (reset! clipboard val))

(defn clipboard-pop []
  (let [val @clipboard]
    (info "clipboard-pop: " val)
    (reset! clipboard nil)
    val))

(defn sin [x]
  (.sin js/Math x))

(defn log! [l & args]
   ;(timbre/log! l :p args {:?line 77})
  (println l args))

(defn info [& args]
  (apply log! :info args))

(defn warn  [& args]
  (apply log! :warn args))

(defn error  [& args]
  (apply log! :error args))

