;; gorilla-repl.fileformat = 2

;; @@ [meta]
{:name "body mass calculator", :tags "cljs, demo, cool, reagent"}

;; @@

;; **
;;; # Body Mass Indicator Demo 
;;; 
;;; Uses cljs kernel with reagent.
;; **

;; @@ [cljs]
; eval with callback. The result shows up in the browser log.
(pinkgorilla.kernel.nrepl.clj-eval "(+ 5 5)" #(.log js/console (str "my result:" %)))
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-string"],"\"96551002-eafc-471f-b9bd-46b22b7c50d0\""],"~:value","\"96551002-eafc-471f-b9bd-46b22b7c50d0\""]
;; <=

;; @@ [cljs]
; evaluate in clojure, and save result to a new reagent atom.
(def x (reagent.core/atom nil))
(pinkgorilla.kernel.nrepl.clj x "+" 7 7)
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#<Atom: nil>"],"~:value","#<Atom: nil>"]
;; <=

;; @@ [cljs]
; show the atom
x
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#<Atom: nil>"],"~:value","#<Atom: nil>"]
;; <=

;; @@ [clj]
; define bmi caluclation - server side (clojure)
(defn calc-bmi-server [bmi-data]
  (println "calc-bmi-server: " bmi-data)
  (let [{:keys [height weight bmi] :as data} bmi-data
        h (/ height 100)]
    (if (nil? bmi)
      (assoc data :bmi (int (/ weight (* h h))))
      (assoc data :weight (int (* bmi h h))))))
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-var"],"#'qil-demo/calc-bmi-server"],"~:value","#'qil-demo/calc-bmi-server"]
;; <=

;; @@ [clj]
; test the server calculation from clojure (sever-server)
(calc-bmi-server {:height 205 :weight 90.1})

;; @@
;; ->
;;; calc-bmi-server:  {:height 205, :weight 90.1}
;; <-
;; =>
;;; ["^ ","~:type","~:list-like","~:open",["~:span",["^ ","~:class","clj-map"],"{"],"~:close",["^3",["^ ","^4","clj-map"],"}"],"~:separator",["^3",", "],"~:items",["~#list",[["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","~:html","~:content",["^3",["^ ","^4","clj-keyword"],":height"],"~:value",":height"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"205"],"^;","205"]]],"^;","[:height 205]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":weight"],"^;",":weight"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-double"],"90.1"],"^;","90.1"]]],"^;","[:weight 90.1]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":bmi"],"^;",":bmi"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-unkown"],"21"],"^;","21"]]],"^;","[:bmi 21]"]]],"^;","{:height 205, :weight 90.1, :bmi 21}"]
;; <=

;; @@ [cljs]
; evaluate on server, but call from client clojurescript. y is an atom
(def y (reagent.core/atom nil)) 
(pinkgorilla.kernel.nrepl.clj y "user/calc-bmi-server" {:height 200 :weight 95.3})
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#<Atom: nil>"],"~:value","#<Atom: nil>"]
;; <=

;; @@ [cljs]
; show the current state of the atom.
y
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#<Atom: nil>"],"~:value","#<Atom: nil>"]
;; <=

;; @@ [cljs]
; client side state of the application.
(def bmi-data (reagent.core/atom {:height 180 :weight 80}))
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'cljs.user/bmi-data"],"~:value","#'cljs.user/bmi-data"]
;; <=

;; @@ [cljs]
; show current state
@bmi-data
;; @@
;; =>
;;; ["^ ","~:type","~:list-like","~:open",["~:span",["^ ","~:class","clj-map"],"{"],"~:close",["^3",["^ ","^4","clj-map"],"}"],"~:separator",["^3",", "],"~:items",["~#list",[["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","~:html","~:content",["^3",["^ ","^4","clj-keyword"],":height"],"~:value",":height"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"180"],"^;","180"]]],"^;","[:height 180]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":weight"],"^;",":weight"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"80"],"^;","80"]]],"^;","[:weight 80]"]]],"^;","{:height 180, :weight 80}"]
;; <=

;; @@ [cljs]
; the slider displays one property of the bmi atom, and updates that property inside the atom
(defn slider [param value min max]
  [:input {:type "range" :value value :min min :max max
           :style {:width "100%"}
           :on-change (fn [e]
                        (println "slider has changed!")
                        (swap! bmi-data assoc param (js/parseInt (.. e -target -value)))
                        (when (not= param :bmi)
                          (swap! bmi-data assoc :bmi nil)))}])
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'cljs.user/slider"],"~:value","#'cljs.user/slider"]
;; <=

;; @@ [cljs]
;(pinkgorilla.notebook.repl/r! 
; (fn []
 ^:r [:div 
  [:p(str  "height: " (:height @bmi-data))]
  [cljs.user/slider :height (:height @bmi-data) 10 220] ; babies are perhaps only 10cm, adults can go up to 220.
  ]


;; @@
;; =>
;;; {}
;; <=

;; @@ [cljs]
; manipulate the height in the slider above, and now lets check the state again...
@bmi-data
;; @@
;; =>
;;; ["^ ","~:type","~:list-like","~:open",["~:span",["^ ","~:class","clj-map"],"{"],"~:close",["^3",["^ ","^4","clj-map"],"}"],"~:separator",["^3",", "],"~:items",["~#list",[["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","~:html","~:content",["^3",["^ ","^4","clj-keyword"],":height"],"~:value",":height"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"180"],"^;","180"]]],"^;","[:height 180]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":weight"],"^;",":weight"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"80"],"^;","80"]]],"^;","[:weight 80]"]]],"^;","{:height 180, :weight 80}"]
;; <=

;; @@ [cljs]
(defn calc-bmi-server []
  (.log js/console "calculating: " @bmi-data)
  (pinkgorilla.kernel.nrepl.clj bmi-data "user/calc-bmi-server" @bmi-data)
  @bmi-data)
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'cljs.user/calc-bmi-server"],"~:value","#'cljs.user/calc-bmi-server"]
;; <=

;; @@ [cljs]
;; the bmi component has 3 sliders, and the bmi indicator changes color.
(defn bmi-component []
  (let [{:keys [weight height bmi]} (calc-bmi-server)
        [color diagnose] (cond
                          (< bmi 18.5) ["orange" "underweight"]
                          (< bmi 25) ["inherit" "normal"]
                          (< bmi 30) ["orange" "overweight"]
                          :else ["red" "obese"])]
    [:div
     [:h3 "BMI calculator"]
     [:div
      "Height: " (int height) "cm"
      [slider :height height 100 220]]
     [:div
      "Weight: " (int weight) "kg"
      [slider :weight weight 30 150]]
     [:div
      "BMI: " (int bmi) " "
      [:span {:style {:color color}} diagnose]
      [slider :bmi bmi 10 50]]]))
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#'cljs.user/bmi-component"],"~:value","#'cljs.user/bmi-component"]
;; <=

;; @@ [cljs]
(pinkgorilla.notebook.repl/r! bmi-component)
;; @@
;; =>
;;; {}
;; <=

;; @@ [cljs]
@bmi-data
;; @@
;; =>
;;; ["^ ","~:type","~:list-like","~:open",["~:span",["^ ","~:class","clj-map"],"{"],"~:close",["^3",["^ ","^4","clj-map"],"}"],"~:separator",["^3",", "],"~:items",["~#list",[["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","~:html","~:content",["^3",["^ ","^4","clj-keyword"],":height"],"~:value",":height"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"186"],"^;","186"]]],"^;","[:height 186]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":weight"],"^;",":weight"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-long"],"80"],"^;","80"]]],"^;","[:weight 80]"],["^ ","^0","^1","^2",null,"^5",null,"^6",["^3"," "],"^7",["^8",[["^ ","^0","^9","^:",["^3",["^ ","^4","clj-keyword"],":bmi"],"^;",":bmi"],["^ ","^0","^9","^:",["^3",["^ ","^4","clj-nil"],"nil"],"^;","nil"]]],"^;","[:bmi nil]"]]],"^;","{:height 186, :weight 80, :bmi nil}"]
;; <=

;; @@ [cljs]
 (pinkgorilla.kernel.nrepl.clj bmi-data "user/calc-bmi-server" {:height 200 :weight 50})
;; @@
;; =>
;;; ["^ ","~:type","~:html","~:content",["~:span",["^ ","~:class","clj-unknown"],"#<Atom: {:height 186, :weight 80, :bmi nil}>"],"~:value","#<Atom: {:height 186, :weight 80, :bmi nil}>"]
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
