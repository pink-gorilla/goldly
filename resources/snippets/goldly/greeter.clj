(require '[goldly.runner :refer [system-start!]])
(require '[goldly.system :as goldly])

(system-start!
 (goldly/system
  {:id :greeter-snippet
   :state {:in ""
           :msg "Type Something..."}
   :html  [:div.rows
           [:input {:class "border border-blue-300"
                    :type "text"
                    :on-change #(?hello % "Hello")
                    :value (:in @state)}]
           [:a {:href (str "/system/greeter-details-snippet/" (:in @state))}
            [:p.m-2.p-1.border.border-round (str "goto person: " (:in @state))]]
           [:div.text-2xl (:msg @state)]]
   :fns   {:hello
           (fn [e s prefix]
             (assoc s
                    :in (:value e)
                    :msg (str prefix ", " (:value e))))}}
  {:fns {}}))


(system-start!
 (goldly/system
  {:id :greeter-details-snippet
   :hidden true
   :state {}
   :html  [:div
           [:p "this shows how to do master-detail relations"]
           [:p "Access this component only via greeter."]
           [:p.bg-blue-300.mg-3 "the best dad in the world is: " ext]]
   :fns {}}
  {:fns {}}))