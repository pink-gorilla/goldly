(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(system-start!
 (goldly/system
  {:id :greeter
   :state {:in ""
           :msg "Type Something..."}
   :html  [:div.rows
           [:input {:class "border border-blue-300"
                    :type "text"
                    :on-change #(?hello % "Hello")
                    :value (:in @state)}]
           [:a {:href (str "/system/greeter-details/" (:in @state))}
            [:p.m-2.p-1.border.border-round (str "goto person: " (:in @state))]]
           [:div.text-2xl (:msg @state)]]
   :fns   {:hello
           (fn [e s prefix]
             (assoc s
                    :in (:value e)
                    :msg (str prefix ", " (:value e))))}}
  ))


