(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(defn dependency [brick-name component-name brick->interface-deps]
  (if (contains? (brick->interface-deps brick-name) component-name)
    "x"
    "·"))

(defn row [brick-name component-names brick->interface-deps]
  (conj (interleave (repeat "")
                    (map #(dependency brick-name % brick->interface-deps)
                         component-names))
        brick-name))

(defn row-color [brick-color]
  (conj (repeat 999 :none) brick-color))

(defn table [components bases color-mode]
  (let [bricks (concat components bases)
        brick->interface-deps (into {} (map (juxt :name #(-> % :interface-deps set)) bricks))
        component-names (sort (map :name components))
        base-names (sort (map :name bases))
        brick-names (concat component-names base-names)
        alignments (repeat 999 :left)
        header-colors (conj (repeat (* 2 (count components)) :yellow) :none)
        header-orientations (conj (interleave (repeat (count components) :horizontal)
                                              (repeat (count components) :vertical)) :horizontal)
        colors (mapv row-color (concat (repeat (count components) :green)
                                       (repeat (count bases) :blue)))
        headers (concat ["brick" "  "] (interpose "  " component-names))
        brick-rows (map #(row % component-names brick->interface-deps)
                        brick-names)]
    (text-table/table " " alignments header-colors header-orientations colors headers brick-rows color-mode)))

(defn print-table [{:keys [components bases]} color-mode]
  (println (table components bases color-mode)))
