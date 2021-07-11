#!/bin/sh

# generates deps.edn in profiles/bundelci

clojure -X:bundel-config


# alternative approach culd be depstar:
# If you do not sync the pom, depstar will take it as-is -- try :sync-pom false (or just remove that option, it should default to false).

