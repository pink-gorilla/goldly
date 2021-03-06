#!/bin/sh

echo "posting: $(cat script/service/sci.edn)"

curl -X POST \
     -d "$(cat script/service/sci.edn)" \
     -H "Content-Type: application/edn" \
     -H 'Connection: keep-alive' \
     -H 'Pragma: no-cache' \
     -H 'Cache-Control: no-cache' \
     -H 'Accept: application/edn' \
     -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36' \
     -H 'Accept-Encoding: gzip, deflate' \
     -H 'Accept-Language: de,en;q=0.9,de-DE;q=0.8,en-DE;q=0.7,en-US;q=0.6' \
     --insecure \
      http://localhost:8000/api/service


#      -d "data=@script/hello.edn" \
#      -H 'Content-Type: application/x-www-form-urlencoded;charset=UTF-8' \
#     --compressed \