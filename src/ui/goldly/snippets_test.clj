(ns ui.goldly.snippets-test
  (:require
   [taoensso.timbre :as timbre :refer [trace debug debugf info infof error]]
   [systems.snippet-registry :refer [add-snippet snippets-by-category]]))

; goldly test

(add-snippet {:type :pinkie
              :category :goldly-test
              :id :error-test
              :filename "snippets/goldly_test/error.edn"})

(add-snippet {:type :pinkie
              :category :goldly-test
              :id :exception-test
              :filename "snippets/goldly_test/exception.edn"})

(add-snippet {:type :goldly-clj
              :category :goldly-test
              :id :binding-test
              :filename "snippets/goldly_test/binding.clj"})

(info "snippets: " (snippets-by-category))