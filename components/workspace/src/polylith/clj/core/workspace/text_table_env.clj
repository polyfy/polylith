(ns polylith.clj.core.workspace.text-table-env
  (:require [clojure.set :as set]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def alignments [:left :left :left :left :left])
(def headers ["environment" "  " "alias" "  " "src"])
(def header-colors [:none :none :none :none :none])
(def row-color-row [:none :none :purple :none :none])

(defn row [{:keys [name alias has-src-dir? has-test-dir?]}
           changed-envs
           indirect-changes
           color-mode]
  (let [changed (if (contains? (set changed-envs) name) " *" "")
        has-src (if has-src-dir? "x" "-")
        has-test (if has-test-dir?
                   (if (contains? indirect-changes name)
                     "+"
                     (color/environment "x" color-mode))
                   (color/environment "-" color-mode))
        env (str (color/environment name color-mode) changed)
        src (str (color/environment has-src color-mode)
                 has-test)]
    [env "" alias "" src]))

(defn table [environments {:keys [changed-environments environments-to-test]} color-mode]
  (let [changed-envs (set changed-environments)
        indirect-changes (set/difference (set environments-to-test) changed-envs)
        row-colors (repeat (count environments) row-color-row)
        rows (mapv #(row % changed-envs indirect-changes color-mode) environments)]
    (text-table/table "  " headers alignments rows header-colors row-colors color-mode)))
