(ns polylith.clj.core.ws-file.from-2-to-3.converter
  (:require [polylith.clj.core.ws-file.from-2-to-3.settings :as settings]
            [polylith.clj.core.ws-file.from-2-to-3.changes :as changes]))

(defn convert [workspace]
  (-> workspace
      (settings/convert)
      (changes/convert)))
