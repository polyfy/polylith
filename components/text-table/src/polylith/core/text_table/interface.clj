(ns polylith.core.text-table.interface
  (:require [polylith.core.text-table.core :as core]))

(defn table [headers alignments rows header-colors row-colors color-mode]
  (core/table headers alignments rows header-colors row-colors color-mode))
