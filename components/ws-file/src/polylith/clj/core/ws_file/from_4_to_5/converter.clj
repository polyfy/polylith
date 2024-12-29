(ns polylith.clj.core.ws-file.from-4-to-5.converter)

(defn convert [workspace]
  (assoc workspace :ws-dialects #{"clj"}))
