(ns systems.fortune
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]
   [systems.fortune-db :as db]))

(system-start!
 (goldly/system
  {:id :fortune
   :state {:cookie nil}
   :html  [:div
           [:h1 "Fortune Cookies"]
           [:p "a demonstration to call server-side clj-functions (clj based)."]
           [:p "This would work with read database queries also :-)"]
           [:button {:class "border m-2 p-3 border-pink-500"
                     :on-click (fn [& _]
                                 (println "getting cookie")
                                 (?cookie 3))} "get a specific cookie"]
           [:button {:class "border m-2 p-3 border-pink-500"
                     :on-click (fn [& _]
                                 (println "getting cookie")
                                 (?cookie))} "get a random cookie"]
           [:p.bg-yellow-500.italic.text-xl.text-blue-700
            (or (:cookie @state) "no cookie received!")]]
   :fns {}}
  {:fns {:cookie [db/get-cookie [:cookie]]}}))

