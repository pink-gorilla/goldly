(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(require '[goldly.service.core :as s])

(defn fun-add [a b]
  (+ a b))

(defn fun-quote []
  "The early fish catches the worm.")

(s/add {:demo/add fun-add})
(s/add {:demo/quote fun-quote})

(system-start!
 (goldly/system
  {:id :service-test
   :hidden true
   :state {:result "no result."}
   :html  [:div
           (run-a state [:result] :demo/add 2 7)
           (run-a state [:saying] :demo/quote)
           [:p "this tests if clj services are working"]
           [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]]
   :fns {}
   :fns-raw {}}
  {:fns {}}))
