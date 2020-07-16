(ns polylith.clj.core.workspace.text-table-env
  (:require [clojure.set :as set]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def alignments [:left :left :left :left :left :left :right :right :right])
(def basic-headers ["environment" "  " "alias" "  " "src"])
(def header-colors [:none :none :none :none :none :none :none :none :none])
(def row-color-row [:none :none :purple :none :purple :none :none :none :none])

(def loc-headers ["  " "loc" "  " "(t)"])

(defn row [{:keys [name alias has-src-dir? has-test-dir? lines-of-code-src lines-of-code-test]}
           changed-envs environments-to-test
           show-loc? color-mode]
  (let [changed (if (contains? (set changed-envs) name) " *" "")
        src (if has-src-dir? "x" "-")
        test (if has-test-dir? "x" "-")
        to-test (if (contains? (set environments-to-test) name) "x" "-")
        env (str (color/environment name color-mode)
                 changed)
        source (str src test to-test)]
    (concat [env "" alias "" source]
            (if show-loc?
              ["" (str lines-of-code-src) "" (str lines-of-code-test)]
              []))))

(defn table [environments {:keys [changed-environments environments-to-test]} total-loc-src total-loc-test show-loc? color-mode]
  (let [changed-envs (set changed-environments)
        indirect-changes (set/difference (set environments-to-test) changed-envs)
        row-colors (repeat (-> environments count inc) row-color-row)
        env-rows (mapv #(row % changed-envs indirect-changes show-loc? color-mode) environments)
        rows (concat env-rows
                     (if show-loc? [["" "" "" "" "" "" (str total-loc-src) "" (str total-loc-test)]]))
        headers (concat basic-headers (if show-loc? loc-headers []))]

    (text-table/table "  " headers alignments rows header-colors row-colors color-mode)))
