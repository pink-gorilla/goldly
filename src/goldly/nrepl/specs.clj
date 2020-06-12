(ns goldly.nrepl.specs)

(def nrepl-ops
  [:stacktrace
   :eval
   :classpath
   :ns-list
   :ns-path
   :lookup
   :complete-doc  ; code complete
   :resources-list

   :ls-middleware
   :add-middleware
   :debug-middleware
   :swap-middleware

   :describe
   :ls-sessions
   :inspect-refresh
   :close

   :init-debugger
   :stdin
   :refresh-clear
   :inspect-get-path
   :debug-instrumented-defs
   :spec-example
   :format-edn

   :inspect-push      ; sounds useful!
   :inspect-next-page
   :ns-aliases
   :refresh

   :retest
   :fn-refs

   :test-all
   :sideloader-start
   :ns-list-vars-by-name

   :eldoc-datomic-query
   :undef

   :inspect-def-current-value
   :set-max-samples
   :fn-deps   ; what is this ? 
   :sideloader-provide
   :ns-load-all
   :debug-input
   :format-code
   :load-file
   :test-var-query
   :ns-vars
   :clojuredocs-refresh-cache
   :eldoc  ; ? 
   :resource
   :clone
   :is-var-profiled
   :out-unsubscribe
   :inspect-prev-page
   :cider-version
   :toggle-profile-ns
   :toggle-profile
   :profile-summary

   :info
   :interrupt
   :ns-vars-with-meta
   :apropos
   :complete
   :inspect-set-page-size
   :pinkieeval
   :macroexpand
   :spec-list
   :toggle-trace-ns
   :track-state-middleware
   :toggle-trace-var
   :slurp
   :get-max-samples
   :spec-form
   :profile-var-summary
   :clojuredocs-lookup
   :clear-profile
   :out-subscribe
   :inspect-pop
   :inspect-clear
   :completions
   :complete-flush-caches
   :test
   :refresh-all
   :test-stacktrace
   :content-type-middleware])

(def describe-response
  {:aux {:cider-version
         {:incremental 0, :major 0, :minor 23, :qualifier [], :version-string "0.23.0"}
         :current-ns "user"}
   :ops {:init-debugger {}, :stdin {}, :refresh-clear {}, :inspect-get-path {}, :debug-instrumented-defs {}, :classpath {}, :spec-example {}, :format-edn {}, :debug-middleware {}, :ns-path {}, :add-middleware {}, :inspect-push {}, :inspect-next-page {}, :ns-aliases {}, :refresh {}, :lookup {}, :retest {}, :fn-refs {}, :swap-middleware {}, :test-all {}, :sideloader-start {}, :ns-list-vars-by-name {}, :resources-list {}, :eldoc-datomic-query {}, :undef {}, :ls-middleware {}, :inspect-refresh {}, :close {}, :complete-doc {}, :ns-list {}, :inspect-def-current-value {}, :set-max-samples {}, :fn-deps {}, :sideloader-provide {}, :ns-load-all {}, :debug-input {}, :format-code {}, :load-file {}, :test-var-query {}, :ns-vars {}, :clojuredocs-refresh-cache {}, :ls-sessions {}, :eldoc {}, :resource {}, :clone {}, :is-var-profiled {}, :out-unsubscribe {}, :inspect-prev-page {}, :cider-version {}
         :toggle-profile-ns {}, :toggle-profile {}, :profile-summary {}, :describe {}, :info {}, :interrupt {}, :stacktrace {}, :ns-vars-with-meta {}, :apropos {}, :complete {}, :inspect-set-page-size {}, :pinkieeval {}, :macroexpand {}, :spec-list {}, :toggle-trace-ns {}, :track-state-middleware {}, :toggle-trace-var {}, :slurp {}, :get-max-samples {}, :spec-form {}, :profile-var-summary {}, :clojuredocs-lookup {}, :clear-profile {}, :out-subscribe {}, :inspect-pop {}, :inspect-clear {}, :completions {}, :complete-flush-caches {}, :test {}, :refresh-all {}, :test-stacktrace {}, :content-type-middleware {}, :eval {}}
   :session "6c72fb90-14d4-4d95-bd09-17c3446a50b6"
   :status ["done"]
   :versions {:clojure {:incremental 1, :major 1, :minor 10, :version-string "1.10.1"}
              :java {:incremental 6, :major 11, :minor 0, :version-string "11.0.6"}
              :nrepl {:incremental 0, :major 0, :minor 8, :version-string "0.8.0-alpha1"}}})

(keys (:ops describe-response))

(def examples
  [{:id "128705fa-4ce6-4ec7-a906-0aa39b3e314d"
    :ns "user"
    :session "ac209158-9ea0-41a7-bcb9-25d0a46eea53"
    :value "\"pinkie render loaded!\""}

   {:id "128705fa-4ce6-4ec7-a906-0aa39b3e314d"
    :session "ac209158-9ea0-41a7-bcb9-25d0a46eea53"
    :status ["done"]}

   {:id "f1160046-3963-4322-9693-5f0046504948"
    :session "396076f2-a4dc-4ba4-ade9-9d371df6149c"
    :status ["done" "error"]
    :unresolved-middleware ["goldly.nrepl.middleware/render-values"]}

   {:err "namespace 'instaparse.gll' not found"
    :id "fc4e26fe-4dad-46b3-ac30-e85bee026eca"
    :session "75a16fd4-15d1-485a-82fc-4f8fde5f0d74"}

   {:ex "class clojure.lang.Compiler$CompilerException"
    :id "fc4e26fe-4dad-46b3-ac30-e85bee026eca"
    :root-ex "class clojure.lang.Compiler$CompilerException"
    :session "75a16fd4-15d1-485a-82fc-4f8fde5f0d74"
    :status ["eval-error"]}

   {:id "fc4e26fe-4dad-46b3-ac30-e85bee026eca"
    :session "75a16fd4-15d1-485a-82fc-4f8fde5f0d74"
    :status ["done"]}

   {:session "dd11ef70-2803-428e-9bb2-f388705ac16c"
    :id "90"
    :ns "snippets.demo"
    :code "(+ 7 17)"
    :value 24
    :pinkie [:span {:class "clj-long"} "24"]
    :out nil}

   {:session "dd11ef70-2803-428e-9bb2-f388705ac16c"
    :id "90", :ns nil, :code "(+ 7 17)"
    :value nil
    :pinkie nil
    :out "20-06-10 03:29:52 lggram INFO [systems.snippets:49] - publish-eval!  nil\n"}

   {:session "dd11ef70-2803-428e-9bb2-f388705ac16c"
    :id "90", :ns nil, :code "(+ 7 17)"
    :value nil
    :pinkie nil
    :out "20-06-10 03:29:52 lggram INFO [goldly.runner:108] - sending  {:run-id nil, :system-id \"f6cf7895-768a-4f65-8507-f5b1adca287f\", :fun nil, :result [[:span {:class \"clj-long\"} \"24\"]], :where [:END]}\n"}



;   
   ])



