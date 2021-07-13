(ns goldly.component.type.notebook
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [debugf infof errorf]]
   [goldly.component.ui :refer [render-component]]
   [ui.notebook.core :refer [notebook-view]]
   [ui.notebook.menu]))

(def opts
  {; if a layout option is passed this will override the settings in localstorage
   :layout  :stacked ; :vertical ; :horizontal :single ;
   :view-only false})

(defn nb [n]
  (let [id-loaded (r/atom true)]
    (fn [n]
      (when-let [id (get-in n [:meta :id])]
        (when-not (= @id-loaded id)
          (reset! id-loaded id)
          (rf/dispatch [:doc/load n])
          nil))
      [notebook-view opts])))

(defmethod render-component :notebook [{:keys [id data args]}]
  (let [n (or (get-in @data [:data]) "")]
    (infof "rendering notebook: id: %s data: %s args: %s" id @data args)
    ;[:div  ; (pr-str @data)
     ;(pr-str n)
    [nb n]
     ;(pr-str (text->notebook id code))
     ;]
    ))

