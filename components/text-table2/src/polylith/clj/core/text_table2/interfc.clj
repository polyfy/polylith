(ns polylith.clj.core.text-table2.interfc
  (:require [polylith.clj.core.text-table2.core :as core]
            [polylith.clj.core.text-table2.column :as column]
            [polylith.clj.core.text-table2.line :as line]))

(defn column [col value cells]
  (column/column col value cells))

(defn line [row cells]
  (line/line row cells))

(defn print-table [initial-spaces color-mode & cells-list]
  (core/print-table initial-spaces color-mode cells-list))
