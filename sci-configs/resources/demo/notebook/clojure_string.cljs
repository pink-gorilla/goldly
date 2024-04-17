(ns demo.notebook.clojure-string
  (:require
   [clojure.string :as str]))

(str/join ", " ["apple" "banana" "blueberry" "wine"])

(str/blank? " ")
(str/blank? " _")