

; babashka script
; bb -f script/json2edn.clj

(def j "{\"foo\":\"bar\"}")

(def j (slurp "/tmp/vega.json"))
(def d (json/parse-string j (fn [k] (keyword  k))))

(println (pr-str d))

