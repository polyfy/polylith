(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn dependency [brick-name component-name brick->interface-deps]
  (if (contains? (brick->interface-deps brick-name) component-name)
    "x"
    "·"))

(defn row [brick-name component-names brick->interface-deps]
  (conj (interleave (repeat "")
                    (map #(dependency brick-name % brick->interface-deps)
                         component-names))
        brick-name))

(defn row-color [brick-color n#columns-with-margin]
  (conj (repeat n#columns-with-margin :none) brick-color))

(defn table [environment components bases color-mode]
  (let [deps (:deps environment)
        component-names (sort (map :name components))
        base-names (sort (map :name bases))
        n#columns-with-margin (* 3 (count components))
        brick-names (concat component-names base-names)
        brick->deps (into {} (mapv (juxt identity #(-> % deps :direct set)) brick-names))
        alignments (repeat n#columns-with-margin :left)
        header-colors (conj (repeat n#columns-with-margin :green) :none)
        header-orientations (conj (interleave (repeat (count components) :horizontal)
                                              (repeat (count components) :vertical)) :horizontal)
        colors (mapv #(row-color % n#columns-with-margin)
                     (concat (repeat (count components) :green)
                             (repeat (count bases) :blue)))
        headers (concat ["brick" "  "] (interpose "  " component-names))
        brick-rows (map #(row % component-names brick->deps)
                        brick-names)]
    (text-table/table " " alignments header-colors header-orientations colors headers brick-rows color-mode)))

(defn print-table [{:keys [environments components bases]} environment-name color-mode]
  (if-let [environment (util/find-first #(= environment-name (:name %)) environments)]
    (println (table environment components bases color-mode))
    (println (str "Couldn't find the " (color/environment environment-name color-mode) " environment."))))
