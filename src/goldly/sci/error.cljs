(ns goldly.sci.error
  (:require
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [webly.user.notifications.core :refer [add-notification]]))

(defn show-sci-error [filename {:keys [error] :as result}]
  (let [{:keys [err root-ex]} error
        {:keys [type line column file phase]} root-ex]
    (error "compilation failed: " result)
    (add-notification :danger (str "sci compile " filename " error: " err " line: " line))))

{:error {:root-ex {:type :sci/error
                   :line 4
                   :column 1
                   :file nil
                   :phase "analysis"}
         :err "Could not resolve symbol: bongotrott"}}