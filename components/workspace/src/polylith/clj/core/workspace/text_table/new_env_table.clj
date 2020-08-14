(ns polylith.clj.core.workspace.text-table.new-env-table
  (:require [polylith.clj.core.workspace.ws-table.shared :as shared]
            [polylith.clj.core.text-table2.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(defn env-cell [environment env-key column row changed-environments color-mode]
  (let [name (env-key environment)
        changed (if (contains? (set changed-environments) name) " *" "")
        env (str (color/environment name color-mode)
                 changed)]
    (shared/standard-cell env column row :none)))

(defn env-column [environments {:keys [changed-environments]} header env-key column color-mode]
  (concat [(shared/header header column)]
          (map-indexed #(env-cell %2 env-key column (+ %1 3) changed-environments color-mode)
                       environments)))

(defn src-cell [index {:keys [name has-src-dir? has-test-dir?]} environments-to-test]
  (let [src (if has-src-dir? "x" "-")
        test (if has-test-dir? "x" "-")
        to-test (if (contains? (set environments-to-test) name) "x" "-")
        source (str src test to-test)]
    (shared/standard-cell source 5 (+ index 3) :purple)))

(defn src-column [environments {:keys [environments-to-test]}]
  (concat [(shared/header "src" 5)]
          (map-indexed #(src-cell %1 %2 environments-to-test)
                       environments)))

(defn loc-cell [index lines-of-code column thousand-sep]
  (shared/number-cell lines-of-code column (+ index 3) :right thousand-sep))

(defn loc-columns [show-loc? environments thousand-sep total-loc-src-environments total-loc-test-environments]
  (when show-loc? (concat [(shared/header "loc" 7 :none :right)]
                          (map-indexed #(loc-cell %1 %2 7 thousand-sep) (map :lines-of-code-src environments))
                          [(shared/number-cell total-loc-src-environments 7 (+ (count environments) 3) :right thousand-sep)]
                          [(shared/header "(t)" 9 :none :right)]
                          (map-indexed #(loc-cell %1 %2 9 thousand-sep) (map :lines-of-code-test environments))
                          [(shared/number-cell total-loc-test-environments 9 (+ (count environments) 3) :right thousand-sep)])))

(defn table [{:keys [settings environments changes total-loc-src-environments total-loc-test-environments]} show-loc?]
  (let [{:keys [color-mode thousand-sep]} settings
        env-col (env-column environments changes "environment" :name 1 color-mode)
        alias-col (env-column environments {} "alias" :alias 3 color-mode)
        src-col (src-column environments changes)
        loc-col (loc-columns show-loc? environments thousand-sep total-loc-src-environments total-loc-test-environments)
        header-spaces (text-table/header-spaces (if show-loc? [2 4 6 8] [2 4]) (repeat "  "))
        cells (text-table/merge-cells env-col alias-col src-col loc-col header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace show-loc?]
  (text-table/print-table (table workspace show-loc?)))
