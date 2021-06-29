#!/bin/sh

rm target/ -r
rm .shadow-cljs/ -r
rm node_modules -r
rm .cpcache -r

rm package.json
rm package-lock.json
rm karma.conf.js
rm shadow-cljs.edn

rm .webly -r
