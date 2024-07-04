(ns
  ^{:author "Mark Sto"}
  integrant.config.core)

;; NB: This should normally use some library to load a system configuration map.
;;     However, we keep things simple for demonstration purposes.
(defn load-config []
  {:postgres {:port 54321}
   :db+creds {:dbname   "postgres"
              :user     "postgres"
              :password "postgres"}})
