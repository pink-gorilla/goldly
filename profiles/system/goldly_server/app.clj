(ns goldly-server.app
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   
 
   [goldly.app :refer [goldly-init! goldly-compile! goldly-run!]]
   ; side-effects
   [goldly-server.routes]
   [goldly-server.keybindings])
  )


