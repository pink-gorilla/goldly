#!/bin/sh

# create pom, including :bundel deps
clojure -Spom -A:bundel

# add git tag to pom
#clojure -M:garamond -t minor
clojure -M:garamond -t

clojure -X:jar  :aliases '[:bundel]'

#clojure -X:goldly :profile '"npm-install"'
#clojure -X:goldly :profile '"release-adv"'
#clojure -X:goldly :profile '"jetty"'