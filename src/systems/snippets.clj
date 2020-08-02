(ns systems.snippets
  (:require
   [clojure.core.async :refer [<! go]]
   [taoensso.timbre :as log :refer [info]]
   [goldly.system :as goldly :refer [def-ui]]
   [goldly.runner :refer [system-start! update-state!]]
   [com.rpl.specter :refer :all]
   [pinkgorilla.nrepl.logger :refer [log-publish!]]
   [pinkgorilla.nrepl.sniffer.middleware :refer [chan-eval-results]]))

(def-ui snippets
  [{:session "3ced9967-e16c-4e47-a661-53b2d8527d96"
    :id 242
    :ns nil
    :code "(println \"Welcome to goldly snippets!\")"
    :value nil
    :pinkie nil
    :out "Welcome to goldly snippets!"}
   {:session "840dadb8-3cd2-486a-bf0f-ccf695804c81"
    :id "86"
    :ns "goldy.nrepl.client"
    :code "(pinkie.converter/R [:p/vega (+ 8 8)])"
    :value [:p/vega 16]
    :picasso [:p/vega
              {:data {:values [{:x 3 :y 4} {:x 7 :y 1}]}
               :mark :point
               :encoding {:x {:field :x :type :quantitative} :y {:field :y :type :quantitative}}}]
    :out nil}
   {:session "3ced9967-e16c-4e47-a661-53b2d8527d96"
    :id "323", :ns "goldy.nrepl.client"
    :code "[66 (+ 8 8)]"
    :value [66 "16"]
    :pinkie [:div "[" [:div {:class "clj-vector"} [:span {:class "clj-keyword"} ":p"] [:span {:class "clj-long"} "16"]] "]"]
    :out nil}])

(def s (goldly/system
        {:name "snippets"
         :route "/snippets"
         :state snippets
         :html  [:p/snippets @state]
         :fns {}}
        {:fns {}}))

(system-start! s)

(defn publish-eval!
  "sends a new eval result to the snippet system.
   [:END] is a specter expression that conj the
   eval-result to browser system state"
  [nrepl-eval-result]
  ;(info "publish-eval! " (:code nrepl-eval-result))
  (update-state! (:id s) {:result [nrepl-eval-result]
                          :where [:END]}))

(go (while true
      (let [n (<! chan-eval-results)]
        ;(println "Read" n)
        (log-publish! n)
        (publish-eval! n))))




