(ns ui.goldly.snippets
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [systems.snippet-registry :refer [add-snippet snippets-by-category]]))

; goldly

(add-snippet {:type :pinkie
              :category :goldly
              :id :hello-world
              :filename "snippets/goldly/hello.edn"})

(add-snippet {:type :goldly
              :category :goldly
              :id :click-counter
              :filename "snippets/goldly/click_counter.clj"})

(add-snippet {:type :goldly
              :category :goldly
              :id :countdown
              :filename "snippets/goldly/countdown.edn"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :fortune
              :filename "snippets/goldly/fortune.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :greeter
              :filename "snippets/goldly/greeter.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :greeter-details
              :filename "snippets/goldly/greeter_details.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :time
              :filename "snippets/goldly/time.clj"})

(add-snippet {:type :goldly-clj
              :category :goldly
              :id :bodymass-index
              :filename "snippets/goldly/bodymass_index.clj"})
