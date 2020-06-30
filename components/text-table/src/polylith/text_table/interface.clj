(ns polylith.text-table.interface
  (:require [polylith.text-table.core :as core]))

(defn table [headers alignments rows header-colors row-colors]
  (core/table headers alignments rows header-colors row-colors))
