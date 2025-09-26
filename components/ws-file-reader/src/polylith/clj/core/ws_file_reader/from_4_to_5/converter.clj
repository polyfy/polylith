(ns ^:no-doc polylith.clj.core.ws-file-reader.from-4-to-5.converter)

(defn convert [{:keys [components bases projects] :as workspace}]
  (assoc workspace :ws-dialects #{"clj"}))
