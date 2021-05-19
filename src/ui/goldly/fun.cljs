(ns ui.goldly.fun
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info warn error]]
   [re-frame.core :as rf]
   [pinkie.pinkie :refer-macros [register-component]]))

; this is a clojurescript namespace
; functions that should be available to all goldly systems


(defn nav [& args]
  (rf/dispatch (into [] (concat [:bidi/goto] args))))

(defn set-system-state [system-id result where]
  (rf/dispatch [:goldly/set-system-state {:system-id system-id
                                          :result result
                                          :where where}]))

(defn modal [f & [size]]
  (if size
    (rf/dispatch [:modal/open [f] :small])
    (rf/dispatch [:modal/open [f]])))

(defn timeout [f ms]
  (js/setTimeout f ms))

#_(defn repeat [f ms]
    (.setTimeout
     js/window
     (fn []
       (f)
       (repeat f ms))
     ms))

(defn parse-float [s]
  (js/parseFloat s))

(defn alert [s]
  (js/alert s))

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

(defn ^{:category :pinkie
        :hidden true}
  exception-component
  "a component that throws exceptions for testing."
  []
  (throw {:type :custom-error
          :message "Something unpleasant occurred"}))

(register-component :p/exc exception-component)
