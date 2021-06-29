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


rm profiles/bundel/node_modules -r
rm profiles/bundel/target -r
rm profiles/bundel/.cpcache -r
rm profiles/bundel/.shadow-cljs -r
rm profiles/bundel/.webly -r
rm profiles/bundel/package.json
rm profiles/bundel/package-lock.json
rm profiles/bundel/karma.conf.js
rm profiles/bundel/shadow-cljs.edn