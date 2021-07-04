(ns goldly.sci.error
  (:require
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [webly.user.notifications.core :refer [add-notification]]))

; {:error {:root-ex {:type :sci/error
;                   :line 4
;                   :column 1
;                   :file nil
;                   :phase "analysis"}
;         :err "Could not resolve symbol: bongotrott"}}

(defn error-view [filename error]
  (let [{:keys [err root-ex]} error
        {:keys [type line column file phase]} root-ex]
    [:div.inline-block "sci cljs compile error"
     [:p "file: " filename " line: " line " column: " column " type: " type]
     [:p "error: " err]]))

(defn show-sci-error [filename {:keys [error] :as result}]
  (error "compilation failed: " result)
  (add-notification :error (error-view filename error) 0))

