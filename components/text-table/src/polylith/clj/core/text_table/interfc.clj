(ns polylith.clj.core.text-table.interfc
  (:require [polylith.clj.core.text-table.core :as core]))

(defn table [headers alignments rows header-colors row-colors color-mode]
  (core/table headers alignments rows header-colors row-colors color-mode))
