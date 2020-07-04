(ns polylith.core.workspace.text-table
  (:require [polylith.core.common.interfc :as common]
            [polylith.core.text-table.interfc :as text-table]
            [polylith.core.util.interfc.color :as color]))

(defn env-data [{:keys [alias component-names base-names]}]
  [alias (set (concat component-names base-names))])

(defn env-contains [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    "x"
    "-"))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} aliases alias->bricks]
  (let [ifc (if (= "component" type)
              (:name interface "-")
              "-")
        loc-src (if lines-of-code-src (str lines-of-code-src) "-")
        loc-test (if lines-of-code-test (str lines-of-code-test) "-")
        all-env-contains (mapv #(env-contains % name alias->bricks) aliases)]
    (vec (concat [ifc "" name "" loc-src "" loc-test ""]
                 (interpose "" all-env-contains)))))

(defn total-loc-row [env-spc-cnt lines-of-code-src lines-of-code-test]
  (vec (concat ["" "" "" "" (str lines-of-code-src) "" (str lines-of-code-test) ""]
               (repeat env-spc-cnt ""))))

(def basic-headers ["interface" "  " "brick" "  " "loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left :right :left :right :left])

(defn env-color [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    :purple
    :none))

(defn env-colors [aliases brick alias->bricks]
  (interpose :none (map #(env-color % brick alias->bricks) aliases)))

(defn component-colors [{:keys [name interface]} aliases alias->bricks]
  (vec (concat [(if interface :yellow :none)]
               [:none :green :none :none :none :none :none]
               (env-colors aliases name alias->bricks))))

(defn base-colors [{:keys [name]} aliases alias->bricks]
  (vec (concat [:none :none :blue :none :none :none :none :none]
               (env-colors aliases name alias->bricks))))

(defn sort-order [x]
  ((juxt :type #(-> % :interface :name) :name) x))

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

(defn print-table [{:keys [settings components bases environments messages lines-of-code-src lines-of-code-test]}]
  (let [envs (filter (complement :test?) environments)
        color-mode (:color-mode settings)
        aliases (mapv :alias envs)
        env-spc-cnt (inc (* (-> envs count dec) 2))
        alignments (concat basic-alignments (repeat env-spc-cnt :center))
        alias->bricks (into {} (map env-data envs))
        sorted-components (sort-by sort-order components)
        sorted-bases (sort-by sort-order bases)
        bricks (concat sorted-components sorted-bases)
        headers (concat basic-headers (interpose "  " aliases))
        brick-rows (mapv #(row % aliases alias->bricks) bricks)
        total-loc-row (total-loc-row env-spc-cnt lines-of-code-src lines-of-code-test)
        plain-rows (conj brick-rows total-loc-row)
        interface->index-components (group-by second (map-indexed index-interface plain-rows))
        rows (map-indexed #(change-interface-name %1 %2 interface->index-components) plain-rows)
        header-colors (repeat (count headers) :none)
        component-colors (mapv #(component-colors % aliases alias->bricks) sorted-components)
        base-colors (mapv #(base-colors % aliases alias->bricks) sorted-bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt) :none))]
        all-colors (concat component-colors base-colors total-loc-colors)
        table (text-table/table headers alignments rows header-colors all-colors color-mode)]
    (println "environments:")
    (doseq [{:keys [alias name]} envs]
      (println (str "  " alias " = " (color/purple color-mode name))))
    (println)
    (println table)
    (println)
    (when (-> messages empty? not)
      (println (common/pretty-messages messages color-mode)))))
