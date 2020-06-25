(ns goldly.user.auth.view
  (:require
   [re-frame.core :as rf]
   [pinkie.pinkie :refer-macros [register-component]]
   [goldly.user.auth.events] ; side-effects
   ))

;; stolen from:
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/views/foursquare.cljs

(defn big-button
  [{:keys [dispatch class icon] :or {class "is-primary"}} label]
  [:div.columns
   [:div.column.is-half.is-offset-one-quarter
    [:a.button.is-medium {:on-click #(rf/dispatch dispatch)
                          :class class}
     [:span.icon
      [:i.fa {:class icon}]]
     [:span label]]]])

(defn- login-button []
  [big-button
   {:dispatch [:auth/login :foursquare]
    :icon "fa-foursquare"}
   "Log In to Foursquare"])

(defn- logout-button []
  [big-button
   {:dispatch [:auth/logout :foursquare]
    :icon "fa-foursquare"
    :class "is-warning"}
   "Log Out of Foursquare"])

(defn- logged-out-page []
  [:div.container.content
   [:h1.title "Log In to Foursquare"]
   [:div
    [:p "To log in to foursquare, hit the link below. You'll be redirected to foursquare to authorize the "
     "application, after which you'll be returned to Haunting Refrain."]
    [login-button]]])

(defn- logged-in-page []
  [:div.container.content
   [:h1.title "Foursquare"]
   [:div
    [:p "Your browser has been authenticated with Foursquare. To log out, use this button:"]
    [logout-button]]])

(defn foursquare-page []
  (let [logged-in (rf/subscribe [:auth/logged-in? :foursquare])]
    (fn []
      (if @logged-in
        [logged-in-page]
        [logged-out-page]))))

(defn hello-page
  "This page is the entry point into hr when the user returns from foursquare authentication."
  []
  (rf/dispatch [:auth/parse-token :foursquare])
  (fn []
    [:div.container.content ""]))


; [:button.github {:on-click (fn [_] (set! (.-location js/window) "/github-login"))}
;          "Login with Github"]]

(defn auth-login []
  [:span.bg-red-700.pt-2.p-2
   [:a {:class "block mt-4 lg:inline-block lg:mt-0 text-pink-600 hover:bg-orange-500 mr-4"
        :on-click #(do
                     (rf/dispatch [:open-oauth-window :github])
                     (rf/dispatch [:auth/login :foursquare])
                      ;(rf/dispatch [::events/set-navbar-menu-active? false])
                     )}
    "Github login"]])

(register-component :p/login auth-login)

