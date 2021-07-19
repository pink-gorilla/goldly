(ns goldly.app
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error]]

   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.service.core]
   [goldly.broadcast.core]
   [goldly.sci.kernel-cljs]
   [goldly.cljs.loader]

   [goldly.extension.lazy]
   [goldly.extension.pinkie :refer [add-extension-pinkie-static]]

   [goldly.component.load]
   [goldly.component.type.system]
   [goldly.component.type.notebook]

   [goldly.notebook-loader.clj-load]
   [goldly.notebook-loader.clj-list :refer [start-watch-notebooks]]
   [ui.notebook.loader.list]

   ; needs refactoring:
   [goldly.system.ui]
   [goldly.system.db]
   [goldly.system.ws]
   [goldly.scratchpad.core]
;
   ))
(def initial-db
  {:id nil
   ; system ui
   :running-systems {}})

(rf/reg-event-db
 :goldly/init
 (fn [db _]
   (let [db (or db {})]
     (assoc-in db [:goldly] initial-db))))

(rf/reg-event-db
 :goldly/dispatch
 (fn [db [_ data]]
   (let [_ (debugf "rcvd :goldly/systems: %s" data)]
     (rf/dispatch [:goldly/clj-result data])
     db)))