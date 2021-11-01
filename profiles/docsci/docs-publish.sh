#!/bin/sh

# be sure that goldly core build is not included
rm ../../target -r

clojure -M:release
