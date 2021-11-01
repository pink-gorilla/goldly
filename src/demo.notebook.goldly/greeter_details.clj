(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(system-start!
 (goldly/system
  {:id :greeter-details
   :hidden true
   :state {}
   :html  [:div
           [:p "this shows how to do master-detail relations"]
           [:p "Access this component only via greeter."]
           [:p.bg-blue-300.mg-3 "the best dad in the world is: " ext]]
   :fns {}}))