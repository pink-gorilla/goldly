(ns goldly.user.settings.events
  "events related to the settings dialog"
  (:require
   [taoensso.timbre :refer-macros [info]]
   [cljs.reader :as rd]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [goldly.user.local-storage :refer [ls-get ls-set!]]))

;; Dialog Visibility Management


(reg-event-db
 :dialog-show
 (fn [db [_ dialog]]
   (assoc-in db [:dialog dialog] true)))

(reg-event-db
 :dialog-hide
 (fn [db [_ dialog]]
   (assoc-in db [:dialog dialog] false)))

;; Settings Dialog Visibility

(reg-event-db
 :app:hide-settings
 (fn [db _]
   (dispatch [:settings-localstorage-save])                ; save to localstorage on close of dialog.
   (assoc-in db [:dialog :settings] false)))

;; Settings Change

(reg-event-db
 :settings-localstorage-load
 (fn [db [_]]
   (let [_ (info "Notebook Settings: Loading from Localstorage ..")
         settings (ls-get :notebook-settings)]
     (if (nil? settings)
       (do (info "Notebook Settings: localstorage empty!")
           db)
       (do (info "Notebook Settings: successfully loaded from localstorage: " (keys settings))
           (assoc-in db [:settings] settings))))))

(reg-event-db
 :settings-localstorage-save
 (fn [db [_]]
   (let [settings (:settings db)]
     (ls-set! :notebook-settings settings)
     (info "localstorage settings saved: " settings)
     db)))

(reg-event-db
 :settings-set
 (fn [db [_ k v]]
   (info "changing setting " k " to: " v)
   (assoc-in db [:settings k] v)))


;; secrets

(reg-event-fx
 :set-clj-secrets
 (fn [{:keys [db]}]
   (let [secrets (get-in db [:settings :secrets])
         secrets (if (nil? secrets) {} secrets)]
     (info "setting clj repl secrets..")
     (clj-eval-ignore-result "pinkgorilla.notebook.secret/set-secrets!" secrets)
     {})))

(reg-event-db
 :secret-add
 (fn [db [_ s]]
   (info "adding secret:" (:name s))
   (dispatch [:set-clj-secrets]) ; push new secrets to clj
   (assoc-in db [:settings :secrets (keyword (:name s))] (:secret s))))

(reg-event-db
 :secret-remove
 (fn [db [_ n]]
   (info "removing secret:" n)
   (dispatch [:set-clj-secrets]) ; push new secrets to clj
   (update-in db [:settings :secrets] dissoc n)))

(reg-event-db
 :secrets-import
 (fn [db [_ s]]
   (info "importing secrets:" s)
   (dispatch [:set-clj-secrets]) ; push new secrets to clj
   (assoc-in db [:settings :secrets] (cljs.reader/read-string s))))

