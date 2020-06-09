(ns systems.snippets
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]))

(println "loading systems.snippets ..")

(def-ui snippets
  [{:session "3ced9967-e16c-4e47-a661-53b2d8527d96"
    :id 242
    :ns nil
    :code "(println \"hello, world!\")"
    :value nil
    :pinkie nil
    :out "hello, world!"}
   {:session "840dadb8-3cd2-486a-bf0f-ccf695804c81"
    :id "86"
    :ns "goldy.nrepl.client"
    :code "(pinkie.converter/R [:p/vega (+ 8 8)])"
    :value [:p/vega 16]
    :pinkie [:p/vega
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

(system-start!
 (goldly/system
  {:name "snippets"
   :state snippets
   :html  [:p/snippets @state]
   :fns {}}
  {:fns {}}))


