(ns systems.snippet-registry
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [clojure.java.io :as io]
   [clojure.core :refer [read-string load-string]]
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]])
  (:import
   [java.util UUID]))

(defn unique-id
  []
  (str (java.util.UUID/randomUUID)))

(defonce snippets (atom []))

(defn add-snippet [{:keys [type category filename id]
                    :or {category :unsorted
                         id (unique-id)} :as snippet}]
  (if (and type filename)
    (->> (assoc snippet :id id :category category)
         (swap! snippets conj))
    (error "snippets need to have type and filename")))

(defn snippets-by-category []
  (group-by :category @snippets))
  ;type category filename id


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

(defn html->system [id src]
  {:id id
   :state {}
   :html (read-string src)
   :fns {}})

(defn start-pinkie [{:keys [src id] :as snippet}]
  (system-start!
   (goldly/system-html
    (html->system id src)
    {:fns {}})))

(defn goldly->system [id src]
  (let [s (read-string src)]
    (assoc s :id id)))

(defn start-goldly [{:keys [src id] :as snippet}]
  (system-start!
   (goldly/system-html
    (goldly->system id src)
    {:fns {}})))

(defn start-goldly-clj [{:keys [src id] :as snippet}]
  (let [;s (read-string src)
        ;x (eval s)
        x (load-string src)]
    snippet))

(defn start-snippet [{:keys [type] :as s}]
  (case type
    :pinkie (start-pinkie s)
    :goldly (start-goldly s)
    :goldly-clj (start-goldly-clj s)
    (error "cannot start snippet of unknown type: " s)))

(defn load-snippet [id]
  (let [id-kw (keyword id)
        s (get-snippet id-kw)]
    (when s
      (start-snippet s)
      s)))

(system-start!
 (goldly/system
  {:id :snippet-registry
   :state {:first true
           :snippets []}
   :html  [:div
           (when (:first @state)
             (swap! state assoc :first false)
             (?get-snippet-list))
           [:h1 "Goldly Snippet Registry"]
           [:p "this snippet registry is also a normal goldly system"]
           [:p "it exists to showcase usecases of goldly snippets"]
           [:p "you can add 3 types of snippets:"]
           [:p "1. static html (hiccup) - good to test pinkie renderers"]
           [:p "2. a goldly system (stateful) that only interacts on the client (cljs only)"]
           [:p "3. a goldly system (stateful) with server interaction"]
           (if (= 0 (count  (:snippets @state)))
             [:p.bg-yellow-500.italic.text-xl.text-blue-700
              "loading snippets .."]
             (into [:div]
                   (map (fn [[cat items]]
                          [:div
                           [:h1.text-blue-700.text-xl cat]
                           (into [:div]
                                 (map (fn [{:keys [id type filename]}]
                                        [:a {:href (str "/system/snippet-viewer/"  (name id))}
                                         [:p filename]])
                                      items))]) (:snippets @state))))]
   :fns {}}
  {:fns {:get-snippet-list [snippets-by-category [:snippets]]}}))

(system-start!
 (goldly/system
  {:id :snippet-viewer
   :hidden true
   :state {:first true
           :snippet {:src "no source"}}
   :html  [:div
           (when (:first @state)
             (swap! state assoc :first false)
             (?load-snippet ext))
           [:h1 "Goldly Snippet Viewer"]
           [:h1.m-2.bg-blue-300
            [:a {:href "/system/snippet-registry/"}
             "snippet-registry"]]
           (let [src (get-in @state [:snippet :src])]
             [:div.border.border-round.m-2.p-2
              [:p/code src]])
           (if ext
             [:p/goldly (keyword ext)]
             [:h1.bg-red-500.m-2 "no goldly system defined ext: " ext])]
   :fns {}}
  {:fns {:load-snippet [load-snippet [:snippet]]}}))

(add-snippet {:type :pinkie
              :category :goldly
              :id :hello-world
              :filename "snippets/goldly/hello.edn"})

(add-snippet {:type :pinkie
              :category :goldly
              :id :error-snippet
              :filename "snippets/goldly/error.edn"})

(add-snippet {:type :goldly
              :category :goldly
              :id :click-counter-snippet
              :filename "snippets/goldly/click_counter.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :fortune-snippet
              :filename "snippets/goldly/fortune.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :time-snippet
              :filename "snippets/goldly/time.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :greeter-snippet
              :filename "snippets/goldly/greeter.clj"})

(info "snippets: " (snippets-by-category))