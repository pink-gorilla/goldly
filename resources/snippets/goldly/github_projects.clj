(require '[taoensso.timbre :as timbre :refer [trace debug debugf info infof error]])
(require '[goldly.system :as goldly])
(require '[goldly.runner :refer [system-start!]])


(defn block [{:keys [name class]}  & children]
  (into [:div.bg-blue-400.m-5.inline-block
         {:class class}
         [:h1.text-2xl name]]
        children))

(defn svg [url-link url-img p]
  [:a {:href (format url-link p)}
   [:img.inline-block
    {:src
     (format url-img p)}]])

(system-start!
 (goldly/system
  {:id :fortune
   :state {:cookie nil}
   :html  [:div
           [:h1 "Fortune Cookies (Snippet)"]
           [?project "webly"]
           [?project "goldly"]
           [?project "gorilla-ui"]
           [?project "nrepl-middleware"]
           [?project "notebook-encoding"]
           [?project "gorilla-explore"]
           [?project "notebook"]
           [?project "lein-pinkgorilla"]
           [?project "pinkie"]
           [?project "picasso"]
           [?project "kernel-cljs-shadow"]]
   :fns-raw {:project (fn [p]
                        [:p ; p
                         [svg "https://github.com/pink-gorilla/%s/actions?workflow=CI"
                          "https://github.com/pink-gorilla/%s/workflows/CI/badge.svg" p]

                         [svg "https://codecov.io/gh/pink-gorilla/%s"
                          "https://codecov.io/gh/pink-gorilla/%s/branch/master/graph/badge.svg" p]

                         [svg "https://clojars.org/org.pinkgorilla/%s"
                          "https://img.shields.io/clojars/v/org.pinkgorilla/%s.svg" p]])
             
             
             
             }}))