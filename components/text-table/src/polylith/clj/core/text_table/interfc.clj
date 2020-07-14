(ns polylith.clj.core.text-table.interfc
  (:require [polylith.clj.core.text-table.core :as core]))

(defn table [initial-spaces headers alignments rows header-colors row-colors color-mode]
  (core/table initial-spaces headers alignments rows header-colors row-colors color-mode))
