(ns demo.hello-user)

(println "demo.hello-user")

(require '[taoensso.timbre :as timbre :refer [trace debug debugf info infof error]])
(require '[goldly.system :as goldly])
(require '[goldly.runner :refer [system-start!]])

(system-start!
 (goldly/system
  {:id :hello-user
   :state {:cookie nil}
   :html  [:div
           [:h1 "Hello!"]
           [:p "You are a User!."]]
   :fns {}}))
