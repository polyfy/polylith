(ns polylith.clj.core.workspace.text-table-env
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.util.interfc.str :as str-util]))

(def alignments (repeat [:left :left :left :left :left :left :right :right :right]))
(def basic-headers ["environment" "  " "alias" "  " "src"])
(def header-colors [:none :none :none :none :none :none :none :none :none])
(def row-color-row [:none :none :purple :none :purple :none :none :none :none])

(def loc-headers ["  " "loc" "  " "(t)"])

(defn row [{:keys [name alias has-src-dir? has-test-dir? lines-of-code-src lines-of-code-test]}
           changed-envs environments-to-test
           thousand-sep show-loc? color-mode]
  (let [changed (if (contains? (set changed-envs) name) " *" "")
        src (if has-src-dir? "x" "-")
        test (if has-test-dir? "x" "-")
        to-test (if (contains? (set environments-to-test) name) "x" "-")
        env (str (color/environment name color-mode)
                 changed)
        source (str src test to-test)]
    (concat [env "" alias "" source]
            (if show-loc?
              ["" (str-util/sep-1000 lines-of-code-src thousand-sep)
               "" (str-util/sep-1000 lines-of-code-test thousand-sep)]
              []))))

(defn table [environments {:keys [changed-environments environments-to-test]} total-loc-src total-loc-test thousand-sep show-loc? color-mode]
  (let [changed-envs (set changed-environments)
        none-colors (repeat :none)
        colors (conj (repeat (-> environments count inc) row-color-row) none-colors header-colors)
        env-rows (mapv #(row % changed-envs environments-to-test thousand-sep show-loc? color-mode) environments)
        headers (concat basic-headers (if show-loc? loc-headers []))
        rows (concat [headers]
                     [(text-table/full-line (conj env-rows headers))]
                     env-rows
                     (if show-loc? [["" "" "" "" "" "" (str total-loc-src) "" (str total-loc-test)]]))]
    (text-table/table "  " alignments colors rows color-mode)))
