(ns goldly.clojure-ns
  (:require 
   [clojure.string]
   [clojure.edn]
   [clojure.walk]
   [goog.object]
   [goog.string]
   [goog.string.format]
   [sci.core :as sci]))



(def clojure-string-namespace
  (sci/copy-ns clojure.string  (sci/create-ns 'clojure.string nil)))

(def clojure-edn-namespace
  (sci/copy-ns clojure.edn  (sci/create-ns 'clojure.edn nil)))

(def clojure-walk-namespace
  (sci/copy-ns clojure.walk  (sci/create-ns 'clojure.walk nil)))

(def goog-object-namespace
  (sci/copy-ns goog.object  (sci/create-ns 'goog.object nil)))

(def goog-string-namespace
   (sci/copy-ns goog.string  (sci/create-ns 'goog.string nil)))

 




#_'clojure.string #_{'split clojure.string/split
                 'join clojure.string/join
                 'escape clojure.string/escape
                 'blank? clojure.string/blank?
                 'replace clojure.string/replace
                 'lower-case clojure.string/lower-case}

#_'clojure.edn #_{'read-string clojure.edn/read-string
              'read clojure.edn/read}

 #_'clojure.walk #_{'postwalk clojure.walk/postwalk
               'prewalk clojure.walk/prewalk
               'keywordize-keys clojure.walk/keywordize-keys
               'walk clojure.walk/walk
               'postwalk-replace clojure.walk/postwalk-replace
               'prewalk-replace clojure.walk/prewalk-replace
               'stringify-keys clojure.walk/stringify-keys}

 #_ 'goog.object #_{'set goog.object/set
   'get goog.object/get}

 ; 'goog.string {'format goog.string/format}