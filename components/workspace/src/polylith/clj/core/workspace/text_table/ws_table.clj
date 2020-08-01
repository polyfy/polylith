(ns polylith.clj.core.workspace.text-table.ws-table
  (:require [clojure.walk :as walk]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.util.interfc.str :as str-util]))

(defn env-brick-names [{:keys [alias component-names base-names]}]
  [alias (set (concat component-names base-names))])

(defn env-brick-test-names [{:keys [alias test-component-names test-base-names]}]
  [alias (set (concat test-component-names test-base-names))])

(defn env-contains [alias brick alias->bricks alias->test-bricks alias->bricks-to-test color-mode]
  (let [src (if (contains? (alias->bricks alias) brick) "x" "-")
        test (if (contains? (alias->test-bricks alias) brick) "x" "-")
        changed (if (contains? (alias->bricks-to-test alias) brick) "x" "-")]
    (str src test changed)))

(defn sep-1000 [number thousand-sep]
  (str-util/sep-1000 number thousand-sep))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} color-mode show-loc? aliases alias->bricks alias->test-bricks changed-components changed-bases alias->bricks-to-test thousand-sep]
  (let [ifc (if (= "component" type)
              (:name interface "-")
              "-")
        changed-bricks (set (concat changed-components changed-bases))
        changed (if (contains? changed-bricks name) " *" "")
        brick (str (color/brick type name color-mode) changed)
        loc-src (if lines-of-code-src lines-of-code-src "-")
        loc-test (if lines-of-code-test lines-of-code-test "-")
        all-env-contains (mapv #(env-contains % name alias->bricks alias->test-bricks alias->bricks-to-test color-mode) aliases)]
    (vec (concat [ifc "" brick ""]
                 (interpose "" all-env-contains)
                 (if show-loc?
                   ["" (sep-1000 loc-src thousand-sep)
                    "" (sep-1000 loc-test thousand-sep) ""]
                   [])))))

(defn ->total-loc-row [show-loc? total-loc-src-bricks total-loc-test-bricks total-locs-src thousand-sep]
  (vec (concat ["" "" "" ""]
               (interpose "" (map #(sep-1000 % thousand-sep) total-locs-src))
               (if show-loc?
                 ["" (sep-1000 total-loc-src-bricks thousand-sep)
                  "" (sep-1000 total-loc-test-bricks thousand-sep) ""]
                 []))))

(def basic-headers ["interface" "  " "brick" "  "])
(def loc-headers ["loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left])
(def loc-alignments [:left :right :left :right])
(def header-orientations (repeat :horizontal))

(defn env-color [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    :purple
    :none))

(defn ->brick-colors [{:keys [interface]} env-spc-cnt show-loc?]
  (vec (concat [(if interface :yellow :none)]
               [:none :none :none]
               (repeat env-spc-cnt :purple)
               (if show-loc?
                 [:none :none :none :none]
                 []))))

(defn sort-order [brick]
  ((juxt :type #(-> % :interface :name) :name) brick))

(defn index-interface [index [interface]]
  [index interface])

(defn clear-repeated-interfaces [index row interface->index-components]
  (let [interface (first row)
        index-components (interface->index-components interface)
        min-index (apply min (map first index-components))]
    (if (= (count index-components) 1)
      row
      (if (or (= index min-index)
              (= "-" interface))
        row
        (assoc row 0 "-\"-")))))

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

(defn alias-changes [[env changes] env->alias]
  [(env->alias env) (set changes)])
(defn ws-table [color-mode components bases environments changed-components changed-bases bricks-to-test total-loc-src-bricks total-loc-test-bricks thousand-sep show-loc?]
  (let [aliases (mapv :alias environments)
        env->alias (into {} (map (juxt :name :alias) environments))
        alias->bricks-to-test (into {} (map #(alias-changes % env->alias) bricks-to-test))
        env-spc-cnt (inc (* (-> environments count dec) 2))
        alignments (concat basic-alignments (repeat env-spc-cnt :center) (if show-loc? loc-alignments))
        alias->bricks (into {} (map env-brick-names environments))
        alias->test-bricks (into {} (map env-brick-test-names environments))
        sorted-components (sort-by sort-order components)
        sorted-bases (sort-by sort-order bases)
        bricks (concat sorted-components sorted-bases)
        headers (->headers show-loc? aliases)
        brick-rows (mapv #(row % color-mode show-loc? aliases alias->bricks alias->test-bricks changed-components changed-bases alias->bricks-to-test thousand-sep) bricks)
        total-locs-src (map :total-lines-of-code-src environments)
        total-loc-row (->total-loc-row show-loc? total-loc-src-bricks total-loc-test-bricks total-locs-src thousand-sep)
        plain-rows (if show-loc? (conj brick-rows total-loc-row) brick-rows)
        interface->index-components (group-by second (map-indexed index-interface plain-rows))
        rows (map-indexed #(clear-repeated-interfaces %1 %2 interface->index-components) plain-rows)
        header-colors (->header-colors show-loc? env-spc-cnt)
        component-colors (mapv #(->brick-colors % env-spc-cnt show-loc?) sorted-components)
        base-colors (mapv #(->brick-colors % env-spc-cnt show-loc?) sorted-bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt) :none))]
        row-colors (concat component-colors base-colors total-loc-colors)]
    (text-table/table "  " alignments header-colors header-orientations row-colors headers rows color-mode)))

(defn print-table [{:keys [settings interfaces components bases environments changes messages total-loc-src-bricks total-loc-test-bricks total-loc-src-environments total-loc-test-environments]} thousand-sep show-loc?]
  (let [color-mode (:color-mode settings)
        {:keys [changed-components changed-bases bricks-to-test]} changes
        table (ws-table color-mode components bases environments changed-components changed-bases bricks-to-test total-loc-src-bricks total-loc-test-bricks thousand-sep show-loc?)
        nof-table (count-table/table interfaces components bases environments color-mode)
        env-table (env-table/table environments changes total-loc-src-environments total-loc-test-environments thousand-sep show-loc? color-mode)]
    (println nof-table)
    (println)
    (println env-table)
    (println)
    (println table)
    (when (-> messages empty? not)
      (println)
      (println (common/pretty-messages messages color-mode)))))

(defn print-table-str-keys [workspace thousand-sep show-loc?]
  (print-table (walk/keywordize-keys workspace) thousand-sep show-loc?))
