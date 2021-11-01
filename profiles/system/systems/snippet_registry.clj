(ns systems.snippet-registry
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [clojure.java.io :as io]
   [clojure.core :refer [read-string load-string]]
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]
   [systems.snippet-runner :refer [start-snippet]]
   [goldly.service.core :as s])
  (:import
   [java.util UUID]))

(defn unique-id
  []
  (str (java.util.UUID/randomUUID)))

(defonce snippets (atom []))

(defn snippet-found? [filename]
  (or (io/resource filename)
      (.exists (io/file filename))))

(defn add-snippet [{:keys [type category filename id]
                    :or {category :unsorted
                         id (unique-id)} :as snippet}]
  (if (and type filename)
    (if (snippet-found? filename)
      (->> (assoc snippet :id id :category category)
           (swap! snippets conj))
      (error "snippet id: " id " file " filename " not found!"))
    (error "snippets need to have type and filename")))

(defn snippets-by-category []
  (let [sc (group-by :category @snippets)]
    sc))

(defn slurp-res-or-file [filename]
  (let [r (io/resource filename)]
    (if r
      (slurp r)
      (slurp filename))))

(defn get-snippet [id]
  (let [s (->> @snippets
               (filter #(= (:id %) id))
               first)]
    (when s
      (assoc s :src (slurp-res-or-file (:filename s))))))

(defn load-snippet [id]
  (let [id-kw (keyword id)
        s (get-snippet id-kw)]
    (when s
      (start-snippet s)
      s)))

(s/add {:get-snippet-list snippets-by-category
        :load-snippet load-snippet})

(system-start!
 (goldly/system
  {:id :snippet-registry
   :state {:first true
           :snippets []}
   :html  [:div.prose
           (when (:first @state)
             (println "*** snippet registry is loading snippet list ..")
             (run-a state [:snippets] :get-snippet-list)
             (swap! state assoc :first false)
             nil)
           (if (= 0 (count  (:snippets @state)))
             [:p.bg-yellow-500.italic.text-xl.text-blue-700
              "loading snippets .."
              (pr-str @state)]
             (into [:div]
                   (map (fn [[cat items]]
                          [:div
                           [:h1.text-blue-700.text-xl.mt-3.mb-3 cat]
                           (into [:div.grid.grid-cols-3]
                                 (map (fn [{:keys [id type filename]}]
                                        [:a {:href (str "/system/snippet-viewer/"  (name id))}
                                         [:p (name id)]])
                                      items))]) (:snippets @state))))
           [:p.mt-5 "There are 3 types of snippets:"]
           [:ol
            [:li [:b "edn"] " - static html (hiccup) - good to test pinkie renderers"]
            [:li [:b "cljs"] " - system (stateful) that only interacts on the client (cljs only)"]
            [:li [:b "clj"] " - a goldly system (stateful) with clj interaction"]]
           [:p "note: this snippet registry is also a normal goldly system :-)"]]
   :fns {}}))

(system-start!
 (goldly/system
  {:id :snippet-viewer
   :hidden true
   :state {:first true
           :snippet {:src "no source"}}
   :html  [:div
           (when (:first @state)
             (swap! state assoc :first false)
             (info "loading snippet: " ext)
             (run-a state [:snippet] :load-snippet ext))
           [:h1.m-2.bg-blue-300
            [:span "type: " (get-in @state [:snippet :type])]
            [:a.border.border-round.m-1 {:on-click ?edit} "edit"]
            [:a.border.border-round.m-1 {:href "/system/snippet-registry"} "snippet-registry"]]
           [:div.grid.grid-cols-2.w-full.h-full.min-h-full.bg-yellow-200 ;.flex.flex-row..items-stretch
            (let [src (get-in @state [:snippet :src])]
              [:div.h-full ; .flex-grow ; border.border-round.m-2.p-2.bg-yellow-500
               [:p/code src]])
            [:div.bg-blue-100.h-full ; .flex-grow  ; flex-grow scales the element to remaining space
             {:style {:overflow-y "auto"}}
             (if ext
               [:p/goldly (keyword ext)]
               [:h1.bg-red-500.m-2 "no goldly system defined ext: state: " (pr-str @state)])]]]
   :fns {:edit (fn []
                 ;(set-system-state :scratchpad (:snippet @state) [:snippet])
                 (clipboard-set (:snippet @state))
                 (nav :goldly/system :system-id :scratchpad)
                 @state)}}))


