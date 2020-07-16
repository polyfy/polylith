(ns polylith.clj.core.workspace.text-table-ws
  (:require [clojure.walk :as walk]
            [polylith.clj.core.workspace.text-table-env :as text-table-env]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(defn env-brick-names [{:keys [alias component-names base-names]}]
  [alias (set (concat component-names base-names))])

(defn env-brick-test-names [{:keys [alias test-component-names test-base-names]}]
  [alias (set (concat test-component-names test-base-names))])

(defn env-contains [alias brick alias->bricks alias->test-bricks alias->bricks-to-test color-mode]
  (let [src (if (contains? (alias->bricks alias) brick) "x" "-")
        test (if (contains? (alias->test-bricks alias) brick) "x" "-")
        changed (if (contains? (alias->bricks-to-test alias) brick) "x" "-")]
    (str src test changed)))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} color-mode show-loc? aliases alias->bricks alias->test-bricks changed-components changed-bases alias->bricks-to-test]
  (let [ifc (if (= "component" type)
              (:name interface "-")
              "-")
        changed-bricks (set (concat changed-components changed-bases))
        changed (if (contains? changed-bricks name) " *" "")
        brick (str (color/brick type name color-mode) changed)
        loc-src (if lines-of-code-src (str lines-of-code-src) "-")
        loc-test (if lines-of-code-test (str lines-of-code-test) "-")
        all-env-contains (mapv #(env-contains % name alias->bricks alias->test-bricks alias->bricks-to-test color-mode) aliases)]
    (vec (concat [ifc "" brick ""]
                 (interpose "" all-env-contains)
                 (if show-loc?
                   ["" loc-src "" loc-test ""]
                   [])))))

(defn ->total-loc-row [show-loc? total-loc-src-bricks total-loc-test-bricks total-locs-src]
  (vec (concat ["" "" "" ""]
               (interpose "" (map str total-locs-src))
               (if show-loc?
                 ["" (str total-loc-src-bricks) "" (str total-loc-test-bricks) ""]
                 []))))

(def basic-headers ["interface" "  " "brick" "  "])
(def loc-headers ["loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left])
(def loc-alignments [:left :right :left :right])

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
(defn ws-table [color-mode components bases environments changed-components changed-bases bricks-to-test total-loc-src-bricks total-loc-test-bricks show-loc?]
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
        brick-rows (mapv #(row % color-mode show-loc? aliases alias->bricks alias->test-bricks changed-components changed-bases alias->bricks-to-test) bricks)
        total-locs-src (map :total-lines-of-code-src environments)
        total-loc-row (->total-loc-row show-loc? total-loc-src-bricks total-loc-test-bricks total-locs-src)
        plain-rows (if show-loc? (conj brick-rows total-loc-row) brick-rows)
        interface->index-components (group-by second (map-indexed index-interface plain-rows))
        rows (map-indexed #(clear-repeated-interfaces %1 %2 interface->index-components) plain-rows)
        header-colors (->header-colors show-loc? env-spc-cnt)
        component-colors (mapv #(->brick-colors % env-spc-cnt show-loc?) sorted-components)
        base-colors (mapv #(->brick-colors % env-spc-cnt show-loc?) sorted-bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt) :none))]
        row-colors (concat component-colors base-colors total-loc-colors)]
    (text-table/table "  " headers alignments rows header-colors row-colors color-mode)))

(defn print-table [{:keys [settings components bases environments changes messages total-loc-src-bricks total-loc-test-bricks total-loc-src-environments total-loc-test-environments]} show-loc?]
  (let [color-mode (:color-mode settings)
        {:keys [changed-components changed-bases bricks-to-test]} changes
        table (ws-table color-mode components bases environments changed-components changed-bases bricks-to-test total-loc-src-bricks total-loc-test-bricks show-loc?)
        env-table (text-table-env/table environments changes total-loc-src-environments total-loc-test-environments show-loc? color-mode)]
    (println env-table)
    (println)
    (println table)
    (when (-> messages empty? not)
      (println)
      (println (common/pretty-messages messages color-mode)))))

(defn print-table-str-keys [workspace show-loc?]
  (print-table (walk/keywordize-keys workspace) show-loc?))
