

(defn lazy-page [r]
  [:div.bg-red-200.w-screen.h-screen.p-5
   [:a.m-1 {:href "/"}  "main"]
   [:p.text-blue-500.text-xxl "show code in highlightjs. highlightjs is lazy."]
   [:p "goto your browser devtools and check what it does"]
   [:p "you should see ui.highlightjs.js and default.css being dynamically laoded
        when you go to this page."]
   [highlightjs "(+ 4 5)\r\n (def a 34) \n{:a 1 :b 2} (defn add [x y] \n (+ x y))"]])

(add-page lazy-page :user/lazy)

