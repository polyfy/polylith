(ns polylith.clj.core.text-table.interfc
  (:require [polylith.clj.core.text-table.core :as core]
            [polylith.clj.core.text-table.line :as line]))

(defn full-line [rows]
  (line/full-line rows))

(defn table [initial-spaces alignments colors rows color-mode]
  (core/table initial-spaces alignments colors rows  color-mode))
