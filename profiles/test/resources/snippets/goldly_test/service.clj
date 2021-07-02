(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(require '[goldly.service.core :as s])

(defn fun-add [a b]
  (+ a b))

(defn fun-quote []
  "The early fish catches the worm.")

(defn fun-ex []
  (throw (Exception. "something bad happened")))

(s/add {:demo/add fun-add
        :demo/quote fun-quote
        :demo/ex fun-ex
        })

(system-start!
 (goldly/system
  {:id :service-test
   :hidden true
   :state {:first true}
   :html  [:div
           (when (:first @state)
             (swap! state assoc :first false)
             (run-a state [:add-result] :demo/add 2 7)
             (run-a state [:saying] :demo/quote)
             (run-a state [:ex-result] :demo/ex)
             nil)
           [:p "this tests if clj services are working"]
           [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]]
   :fns {}
   :fns-raw {}}
  ))
