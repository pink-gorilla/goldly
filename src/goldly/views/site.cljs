(ns goldly.views.site
  (:require
   [webly.user.template :as template]))

(def h-splash
  {:nav {:brand "Goldly"
         :brand-link "/"
         :items [{:text "running systems" :link "/goldly"}
                 {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special true}]}
   :splash {:link-text "On Github"
            :link-url "https://github.com/pink-gorilla/goldly"
            :title ["Goldly lets you create "
                    [:br]
                    "realtime dashboards powered by clojure"]
            :title-small "open source"}})

(def h (dissoc h-splash :splash))

(defn header-splash []
  [template/header h-splash])

(defn header []
  [template/header h])

#_(defn systems-menu []
    [:a.pr-2.text-right.text-blue-600.text-bold.tracking-wide.font-bold.border.border-blue-300.rounded.cursor-pointer
     {:on-click #(dispatch [:bidi/goto :goldly/system-list])
      :style {:position "absolute"
              :z-index 200 ; dialog is 1040 (we have to be lower)
              :top "10px"
              :right "10px"
              :width "80px"
              :height "30px"}} "Systems"])

