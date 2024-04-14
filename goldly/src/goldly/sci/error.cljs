(ns goldly.sci.error
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf info error]]
   [frontend.notification :refer [show-notification]]))

; {:error {:root-ex {:type :sci/error
;                   :line 4
;                   :column 1
;                   :file nil
;                   :phase "analysis"}
;         :err "Could not resolve symbol: bongotrott"}}

(defn sci-error [error]
  (let [{:keys [err root-ex]} error
        {:keys [type line column file phase]} root-ex]
    [:div.inline-block
     [:p.text-red-500.text-bold err]
     (when root-ex
       [:p "phase: " phase " type: " type])
     (when root-ex
       [:p "file: " file "line: " line " column: " column])]))

(defn error-view [filename error]
  [:div.inline-block
   [:p "sci cljs compile error in file: " filename]
   [sci-error error]])

(defn show-sci-error [filename error]
  (timbre/error "compilation failed: " filename error)
  (show-notification :error (error-view filename error) 0))

(defn exception->error [e]
  ; #error {:message "Could not resolve symbol: call-bad-fn", 
  ;          :data {:type :sci/error, :line nil, :column nil, :file nil, :phase "analysis"}}
  ; not working:
  ; error-message (:error/message err)
  ; error-data (:error/data err)
  (let [data (ex-data e)]
    (when-let [message (or (:message data) (.-message e))]
      (let [data (or (:data data) (.-data e))]
        (error "error-message:" message)
        (error "error-data:" data)
        {:err message
         :root-ex data}))))