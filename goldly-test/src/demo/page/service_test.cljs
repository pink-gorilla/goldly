(ns demo.page.service-test
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.service.core :refer [clj clj-atom run-a run-a-map]]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn make-requests [state]
  (swap! state assoc :first false)
  (run-a state [:add-result]  'demo.service/add 2 7)
  (run-a state [:saying] 'demo.service/quote)
  (run-a state [:ex-result] 'demo.service/ex)
  (run-a-map {:a state
              :path [:saying2]
              :fun 'demo.service/quote-slow
              :timeout 1000})
  nil)

(defn promise-status [pr]
  [:div "promise status:"
   " resolved?: " (pr-str (p/resolved? pr))
    ; " rejected?: " (pr-str (p/rejected? pr))
   " pending?: " (pr-str (p/pending? pr))
   " done?: " (pr-str (p/done? pr))
   " promise?: " (pr-str (p/promise? pr))
   " thenable?: " (pr-str (p/thenable? pr))])

(defn show-page [state saying saying2]
  (fn [state saying saying2]
    [:div
     [:p "this tests if clj services are working"]
     [:p "saying via promise: " (pr-str @saying)]
     [promise-status saying]
     [:p "saygin via promise-atom: " (pr-str @saying2)]
     [:p "you should see one error message (for a service that is not defined)"]
     (when (:first @state)
       (make-requests state))
     [:p.bg-blue-300.mg-3 "state: " (pr-str @state)]]))

(defn service-page [_route]
  (let [state (r/atom {:first true})
        saying (clj 'demo.service/quote)
        saying2 (clj-atom 'demo.service/quote)
      ;  _ (p/then saying (fn [d] (swap! state assoc :clj d)))
        _ (p/catch saying (fn [e] (swap! state assoc :clj-err e)))]
    (fn [& _]
      [show-page state saying saying2])))

(def service-page
  (wrap-layout service-page))
