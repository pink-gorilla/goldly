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

rm profiles/bundelci/node_modules -r
rm profiles/bundelci/target -r
rm profiles/bundelci/.cpcache -r
rm profiles/bundelci/.shadow-cljs -r
rm profiles/bundelci/.webly -r
rm profiles/bundelci/package.json
rm profiles/bundelci/package-lock.json
rm profiles/bundelci/karma.conf.js
rm profiles/bundelci/shadow-cljs.edn

rm profiles/test/node_modules -r
rm profiles/test/target -r
rm profiles/test/.cpcache -r
rm profiles/test/.shadow-cljs -r
rm profiles/test/.webly -r
rm profiles/test/package.json
rm profiles/test/package-lock.json
rm profiles/test/karma.conf.js
rm profiles/test/shadow-cljs.edn