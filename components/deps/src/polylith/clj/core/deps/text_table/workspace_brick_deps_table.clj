(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn dependency [brick-name component-name brick->deps brick->indirect-deps]
  (cond
    (contains? (brick->deps brick-name) component-name) "x"
    (contains? (brick->indirect-deps brick-name) component-name) "+"
    :else "·"))

(defn row [brick-name component-names brick->deps brick->indirect-deps]
  (conj (interleave (repeat "")
                    (map #(dependency brick-name % brick->deps brick->indirect-deps)
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
        brick->indirect-deps (into {} (mapv (juxt identity #(-> % deps :indirect set)) brick-names))
        alignments (repeat n#columns-with-margin :left)
        header-colors (conj (repeat n#columns-with-margin :green) :none)
        header-orientations (conj (interleave (repeat (count components) :horizontal)
                                              (repeat (count components) :vertical)) :horizontal)
        colors (mapv #(row-color % n#columns-with-margin)
                     (concat (repeat (count components) :green)
                             (repeat (count bases) :blue)))
        headers (concat ["brick" "  "] (interpose "  " component-names))
        brick-rows (map #(row % component-names brick->deps brick->indirect-deps)
                        brick-names)]
    (text-table/table " " alignments header-colors header-orientations colors headers brick-rows color-mode)))

(defn print-table [{:keys [environments components bases]} environment-name color-mode]
  (if-let [environment (common/find-environment environment-name environments)]
    (println (table environment components bases color-mode))
    (println (str "Couldn't find the " (color/environment environment-name color-mode) " environment."))))
