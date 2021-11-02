(ns goldly-server.keybindings
  (:require
   [notebook.keybindings :as nb]))

(def goldly-keybindings-app
  [{:kb "alt-g k" :handler [:palette/show]           :desc "Keybindings dialog"}
   {:kb "esc"     :handler [:modal/close]            :desc "Dialog Close"} ; for ALL dialogs!
   {:kb "alt-g t" :handler [:reframe10x-toggle] :desc "10x visibility toggle"}
                ; goldly:
   {:kb "alt-g a" :handler [:bidi/goto "/"]  :desc "goto user app"}
   {:kb "alt-g m" :handler [:bidi/goto :goldly/about]  :desc "goto main"}
   {:kb "alt-g r" :handler [:bidi/goto :goldly/repl]  :desc "goto repl"}
   {:kb "alt-g s" :handler [:bidi/goto :goldly/system :system-id :snippet-registry] :desc "goto snippets"}])

(def keybindings-default
  (into []
        (concat
         goldly-keybindings-app
         nb/keybindings-notebook)))