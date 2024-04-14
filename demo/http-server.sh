#!/usr/bin/env bb

(require '[babashka.deps :as deps])
(deps/add-deps
 '{:deps {org.babashka/http-server {:mvn/version "0.1.11"}}})

(require '[babashka.http-server :as http-server])

(http-server/exec {:port 8080
              :dir "./target/static"})
