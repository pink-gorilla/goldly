


(defn info-c [r]
  [:div.bg-red-200.w-screen.h-screen.p-5
   [:p.text-blue-500.text-xxl "sci compile test."]
   [:p "test for compilation: " (pr-str (compile-sci "(+ 5 5)"))]])

(add-page-test info-c :user/scicompile)