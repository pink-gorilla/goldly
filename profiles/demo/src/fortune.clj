
(require '[fortunedb :as db])

(require '[goldly.service.core :as s])


(s/add {:cookie/get db/get-cookie})