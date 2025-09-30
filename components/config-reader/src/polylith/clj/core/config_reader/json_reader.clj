(ns ^:no-doc polylith.clj.core.config-reader.json-reader
  (:require [clojure.data.json :as json]))

(defn slurp-file [filepath]
  (try
    (let [content (slurp filepath)]
      {:content content})
    (catch Exception e
      {:error (str "Couldn't open '" filepath "': " (.getMessage e))})))


(defn read-file [filepath]
  (try
    (let [{:keys [content error]} (slurp-file filepath)]
      (if error
        {:error error}
        (try
          {:config (json/read-str content :key-fn keyword)}
          (catch Exception e
            {:error (str "Couldn't parse '" filepath "': " (.getMessage e))}))))))
