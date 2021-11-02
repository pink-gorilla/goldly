(ns goldly-server.app
  (:require
   [taoensso.timbre :as timbre :refer-macros [info infof]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [re-frame.core :as rf]

   [goldly.app]

   [goldly.notebook-loader.clj-list :refer [start-watch-notebooks]]
   [goldly-server.developer-tools]))

(rf/reg-event-db
 :goldly-server/init
 (fn [db [_]]
   (info "goldly starting ..")
   (rf/dispatch [:goldly/init])
   db))

