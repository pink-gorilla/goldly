(ns demo.notebook.pprint
  (:require
   [cljs.pprint :refer [print-table]]))

(print-table [{:name "Harry" :age "?"}
              {:name "Dumbeldor" :age "old"}])