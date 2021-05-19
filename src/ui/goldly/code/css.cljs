(ns ui.goldly.code.css
  (:require
   [webly.user.css.helper :refer [add-themes]]
   [ui.goldly.code.themes :refer [themes]]))

(def components
  {:highlight (add-themes
               {}
               "highlight.js/styles/%s.css"
               themes)
   ;:code-linenumber  {true ["goldly/code.css"]}
   })

(def config
  {:highlight "github" ; "default"   
   ;:code-linenumber true
   })