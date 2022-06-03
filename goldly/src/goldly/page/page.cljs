(ns goldly.page.page
  (:require
   [reagent.core :as r]
   [taoensso.timbre :as timbre :refer-macros [error]]
   [frontend.page :refer [reagent-page]]))

; shows how to impement error boundary:
; https://github.com/reagent-project/reagent/blob/c214466bbcf099eafdfe28ff7cb91f99670a8433/test/reagenttest/testreagent.cljs

; from component-did-catch
; #js {:componentStack 
       ; cmp@http://localhost:8000/r/cljs-runtime/reagent.impl.component.js:508:43
       ; cmp@http://localhost:8000/r/cljs-runtime/reagent.impl.component.js:508:43
       ; cmp@http://localhost:8000/r/cljs-runtime/reagent.impl.component.js:508:43
       ; div

(defn error-boundary [_ #_comp]
  (let [error-a (r/atom nil)
        info-a (r/atom nil)]
    (r/create-class
     {:component-did-catch (fn [_  _ i] #_this #_e  ; [this e i]
                             ; i is a js object with stacktrace
                             (error "page did catch: " i)
                             (reset! info-a i))

      :get-derived-state-from-error (fn [e]
                                      ; to saves the exception data; gets shows in the dom
                                      ;(println "pinkie component get-derived-state-from-error: " e)
                                      (reset! error-a e)
                                      #js {})
      :reagent-render (fn [comp]
                        (if @error-a
                          [:div.bg-red-300
                           "Error: "
                           (when @error-a (pr-str @error-a))]
                          comp))})))

(defn show-page
  "shows a page 
   expects: kw and route-map"
  [route-map]
  (reagent-page route-map))

;; this is already in webly.
(defn available-pages
  "currently available pages that can be used in the routing table
   seq of page keywords"
  []
  (->> (methods reagent-page)
       keys
       (remove #(= :default %))
       (into [])))

(defn add-page
  "add-page is exposed to sci
   defines a new browser-based page 
   that can be used in the routing table to define new pages"
  [p kw]
  (defmethod reagent-page kw [{:keys [_route-params _query-params _handler] :as route}]
    [error-boundary
     [p route]]))


