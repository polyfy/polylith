(ns polylith.clj.core.workspace.text-table
  (:require [clojure.walk :as walk]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(defn env-data [{:keys [alias component-names base-names]}]
  [alias (set (concat component-names base-names))])

(defn env-contains [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    "x"
    "-"))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} show-loc? aliases alias->bricks]
  (let [ifc (if (= "component" type)
              (:name interface "-")
              "-")
        loc-src (if lines-of-code-src (str lines-of-code-src) "-")
        loc-test (if lines-of-code-test (str lines-of-code-test) "-")
        all-env-contains (mapv #(env-contains % name alias->bricks) aliases)]
    (vec (concat [ifc "" name ""]
                 (interpose "" all-env-contains)
                 (if show-loc?
                   ["" loc-src "" loc-test ""]
                   [])))))

(defn ->total-loc-row [show-loc? env-spc-cnt lines-of-code-src lines-of-code-test]
  (vec (concat ["" "" "" ""]
               (repeat env-spc-cnt "")
               (if show-loc?
                 ["" (str lines-of-code-src) "" (str lines-of-code-test) ""]
                 []))))

(def basic-headers ["interface" "  " "brick" "  "])
(def loc-headers ["loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left])
(def loc-alignments [:left :right :left :right])

(defn env-color [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    :purple
    :none))

(defn env-colors [show-loc? aliases brick alias->bricks]
  (let [colors (map #(env-color % brick alias->bricks) aliases)]
    (if show-loc?
      (interleave colors
                  (repeat :none))
      (interpose :none colors))))

(defn ->component-colors [{:keys [name interface]} show-loc? aliases alias->bricks]
  (vec (concat [(if interface :yellow :none)]
               [:none :green :none]
               (env-colors show-loc? aliases name alias->bricks)
               (if show-loc?
                 [:none :none :none :none]
                 []))))

(defn ->base-colors [{:keys [name]} show-loc? aliases alias->bricks]
  (vec (concat [:none :none :blue :none]
               (env-colors show-loc? aliases name alias->bricks)
               (if show-loc?
                 [:none :none :none :none]
                 []))))

(defn sort-order [brick]
  ((juxt :type #(-> % :interface :name) :name) brick))

(defn index-interface [index [interface]]
  [index interface])

(defn change-interface-name [index row interface->index-components]
  (let [interface (first row)
        index-components (interface->index-components interface)
        min-index (apply min (map first index-components))]
    (if (= (count index-components) 1)
      row
      (if (or (= index min-index)
              (= "-" interface))
        row
        (assoc row 0 "")))))

(defn ->headers [show-loc? aliases]
  (if show-loc?
    (concat basic-headers (interleave aliases (repeat "  ")) loc-headers)
    (concat basic-headers (interpose "  " aliases))))

(defn ->header-colors [show-loc? env-spc-cnt]
  (concat (repeat (count basic-headers) :none)
          (repeat env-spc-cnt :purple)
          (if show-loc?
            [:none :none :none :none :none]
            [])))

(defn ws-table [color-mode components bases environments lines-of-code-src lines-of-code-test show-loc?]
  (let [aliases (mapv :alias environments)
        env-spc-cnt (inc (* (-> environments count dec) 2))
        alignments (concat basic-alignments (repeat env-spc-cnt :center) (if show-loc? loc-alignments))
        alias->bricks (into {} (map env-data environments))
        sorted-components (sort-by sort-order components)
        sorted-bases (sort-by sort-order bases)
        bricks (concat sorted-components sorted-bases)
        headers (->headers show-loc? aliases)
        brick-rows (mapv #(row % show-loc? aliases alias->bricks) bricks)
        total-loc-row (->total-loc-row show-loc? env-spc-cnt lines-of-code-src lines-of-code-test)
        plain-rows (if show-loc? (conj brick-rows total-loc-row) brick-rows)
        interface->index-components (group-by second (map-indexed index-interface plain-rows))
        rows (map-indexed #(change-interface-name %1 %2 interface->index-components) plain-rows)
        header-colors (->header-colors show-loc? env-spc-cnt)
        component-colors (mapv #(->component-colors % show-loc? aliases alias->bricks) sorted-components)
        base-colors (mapv #(->base-colors % show-loc? aliases alias->bricks) sorted-bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt) :none))]
        all-colors (concat component-colors base-colors (if show-loc? total-loc-colors []))]
    (text-table/table headers alignments rows header-colors all-colors color-mode)))

(defn print-table [{:keys [settings components bases environments messages lines-of-code-src lines-of-code-test]} show-loc?]
  (let [color-mode (:color-mode settings)
        table (ws-table color-mode components bases environments lines-of-code-src lines-of-code-test show-loc?)]
    (println "environments:")
    (doseq [{:keys [alias name]} environments]
      (println (str "  " alias " = " (color/purple color-mode name))))
    (println)
    (println table)
    (when (-> messages empty? not)
      (println)
      (println (common/pretty-messages messages color-mode)))))

(defn print-table-str-keys [workspace show-loc?]
  (print-table (walk/keywordize-keys workspace) show-loc?))
