(ns systems.snippet-scratchpad
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [clojure.java.io :as io]
   [clojure.core :refer [read-string load-string]]
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]
   [systems.snippet-runner :refer [start-snippet]]))

(system-start!
 (goldly/system
  {:id :scratchpad
   :state {:new-system true
           :running nil
           :snippet {:id :scratchpad-system
                     :type :pinkie
                     :src "[:h1.color-red \"hello, world!\"]"}}
   :html  [:div
           (when-let [v (clipboard-pop)]
             (info "received snippet from clipboard: " v)
             (swap! state assoc :snippet (assoc v :id :scratchpad-system)))
           [:h1.m-2.bg-blue-300
            [:span "type: " (get-in @state [:snippet :type])]
            [:button.border.border-round.m-1
             {:on-click (fn [& _]
                          (?start-snippet (:snippet @state))
                          (swap! state assoc :new-system true))}
             "run"]
            [:a.border.border-round.m-1.right-0 {:href "/system/snippet-registry"} "snippet-registry"]]

           [:div.grid.grid-cols-2.w-full.h-full.min-h-full.bg-yellow-200 ;.flex.flex-row..items-stretch
            [:div.h-full ; .flex-grow ; border.border-round.m-2.p-2.bg-yellow-500
             [:p/codemirror 0 state [:snippet :src]]]
            [:div.bg-blue-100.h-full ; .flex-grow  ; flex-grow scales the element to remaining space
             {:style {:overflow-y "auto"}}
             (if (:new-system @state)
               (do (swap! state assoc :new-system false)
                   [:p "no system is running"])
               [:<>
                [:p/goldly :scratchpad-system]
                [:p/frisk @state]])]]]
   :fns {:eval (fn []
                 (info "eval:" @state)
                 @state)}}
  {:fns {:start-snippet [start-snippet [:running]]}}))