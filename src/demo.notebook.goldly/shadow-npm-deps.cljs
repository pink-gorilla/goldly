;; gorilla-repl.fileformat = 2

;; @@ [meta]
{}

;; @@

;; **
;;; 
;;; # How to use NPM dependencies in Shadow CLJS Kernel
;;; 
;;; The Shadow CLJS kernel uses Self Hosted Clojurescript in the Notebook Browser. 
;;; Dependencies are loaded via Bundles that are created by kernel-shadow-deps. 
;;; This bundles can contain both Clojurescript and NPM modules.
;; **

;; @@ [cljs]
; any eval will trigger dependency loading
(+ 4 4)
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-long"],"8"],"~:value","8"]
;; <=

;; @@ [cljs]
(ns unsightly-resonance  
  (:require 
    [fortune.core :as f]
    [module$node_modules$moment$moment :as m]
   )) 
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-nil"],"nil"],"~:value","nil"]
;; <=

;; @@ [cljs]
(f/cookie)
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-string"],"\"The best way to get rid of an enemy is to make a friend.\""],"~:value","\"The best way to get rid of an enemy is to make a friend.\""]
;; <=

;; @@ [cljs]
; 2019 03 31 awb99: broken - not sure why. this used to work.
(.now m)
;; @@

;; @@ [cljs]
(.log js/console "This prints out to browser console.")
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-nil"],"nil"],"~:value","nil"]
;; <=

;; **
;;; # option 2 to get npm refs -> shadow.js
;; **

;; @@ [cljs]
; 2019 03 31 awb99: broken - not sure why. this used to work.
; listing works ony when no compression used in js bundle
;(. js/shadow.js -files)
;; @@

;; @@ [cljs]
; to work with a npm module we define it to a cljs def
; the name depends on compile settings of the bundle
; if compression is enabled, then instead of names we have numbers
(def moment (.jsRequire js/shadow.js "module$node_modules$moment$moment"))
;(def moment (.jsRequire js/shadow.js 1))
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'unsightly-resonance/moment"],"~:value","#'unsightly-resonance/moment"]
;; <=

;; @@ [cljs]
(.now moment)
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-long"],"1585642795400"],"~:value","1585642795400"]
;; <=

;; @@ [clj]

;; @@

;; @@ [clj]

;; @@
;; ->
;;; 
;; <-
;; =>
;;; ["^ ","~:type","html","~:value",["~:span"]]
;; <=
