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

(defn env-status-flags [alias brick alias->bricks alias->test-bricks alias->bricks-to-test]
  (let [has-src (if (contains? (alias->bricks alias) brick) "x" "-")
        has-test-src (if (contains? (alias->test-bricks alias) brick) "x" "-")
        to-test (if (contains? (alias->bricks-to-test alias) brick) "x" "-")]
    (str has-src has-test-src to-test)))

(defn sep-1000 [number thousand-sep]
  (str-util/sep-1000 number thousand-sep))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} color-mode show-loc? aliases profile-names alias->bricks alias->test-bricks alias->bricks-to-test profile->bricks profile->test-bricks profile->bricks-to-test changed-components changed-bases thousand-sep]
  (let [ifc (if (= "component" type)
              (:name interface "-")
              "-")
        changed-bricks (set (concat changed-components changed-bases))
        changed (if (contains? changed-bricks name) " *" "")
        brick (str (color/brick type name color-mode) changed)
        loc-src (if lines-of-code-src lines-of-code-src "-")
        loc-test (if lines-of-code-test lines-of-code-test "-")
        all-env-contains (mapv #(env-status-flags % name alias->bricks alias->test-bricks alias->bricks-to-test) aliases)
        all-pro-contains (mapv #(env-status-flags % name profile->bricks profile->test-bricks alias->bricks-to-test) profile-names)]
    (vec (interpose "" (concat [ifc brick] all-env-contains all-pro-contains
                               (when show-loc?
                                 [(sep-1000 loc-src thousand-sep)
                                  (sep-1000 loc-test thousand-sep)]))))))

(defn ->total-loc-row [show-loc? profiles total-loc-src-bricks total-loc-test-bricks total-locs-src thousand-sep]
  (vec (interpose "" (concat ["" ""]
                             (map #(sep-1000 % thousand-sep) total-locs-src)
                             (repeat (count profiles) "")
                             (when show-loc?
                               [(sep-1000 total-loc-src-bricks thousand-sep)
                                (sep-1000 total-loc-test-bricks thousand-sep)])))))

(def basic-headers ["interface" "  " "brick" "  "])
(def loc-headers ["loc" "(t)"])
(def basic-alignments [:left :left :left :left])
(def loc-alignments [:left :right :left :right])
(def header-orientations (repeat :horizontal))

(defn env-color [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    :purple
    :none))

(defn ->brick-colors [{:keys [interface]} env-spc-cnt profile-spc-cnt show-loc?]
  (vec (concat [(if interface :yellow :none)]
               [:none :none :none]
               (repeat env-spc-cnt :purple)
               (repeat profile-spc-cnt :cyan)
               (if show-loc?
                 [:none :none :none :none]
                 []))))

(defn sort-order [brick]
  ((juxt :type #(-> % :interface :name) :name) brick))

(defn ->headers [show-loc? aliases profiles]
  (let [profile-headers (map #(-> % first clojure.core/name) profiles)]
    (concat basic-headers
            (interpose "  " (concat aliases
                                    profile-headers
                                    (if show-loc? loc-headers []))))))

(defn ->header-colors [show-loc? env-spc-cnt profile-spc-cnt]
  (concat (repeat (count basic-headers) :none)
          (repeat env-spc-cnt :purple)
          (repeat profile-spc-cnt :cyan)
          (if show-loc?
            [:none :none :none :none :none]
            [])))

(defn profile-sorting [[profile]]
  [(not= :default profile) profile])

(defn alias-changes [[env changes] env->alias]
  [(env->alias env) (set changes)])

(defn profile-name-bricks [[profile {:keys [src-bricks]}]]
  [(clojure.core/name profile) src-bricks])

(defn profile-name-test-bricks [[alias {:keys [test-bricks]}]]
  [(clojure.core/name alias) test-bricks])

(defn ws-table [color-mode components bases environments profile->settings changed-components changed-bases env->bricks-to-test total-loc-src-bricks total-loc-test-bricks thousand-sep show-loc?]
  (let [aliases (mapv :alias environments)
        env->alias (into {} (map (juxt :name :alias) environments))
        alias->bricks-to-test (into {} (map #(alias-changes % env->alias) env->bricks-to-test))
        env-spc-cnt (inc (* (-> environments count dec) 2))
        alias->bricks (into {} (map env-brick-names environments))
        alias->test-bricks (into {} (map env-brick-test-names environments))
        sorted-components (sort-by sort-order components)
        sorted-bases (sort-by sort-order bases)
        bricks (concat sorted-components sorted-bases)
        profiles (sort-by profile-sorting profile->settings)
        profile-spc-cnt (* (count profiles) 2)
        alignments (concat basic-alignments (repeat (+ env-spc-cnt profile-spc-cnt) :center) (if show-loc? loc-alignments))
        profile-names (mapv #(-> % first name) profiles)
        profile->bricks (into {} (map profile-name-bricks profiles))
        profile->test-bricks (into {} (map profile-name-test-bricks profiles))
        profile->bricks-to-test {}
        headers (->headers show-loc? aliases profiles)
        brick-rows (mapv #(row % color-mode show-loc? aliases profile-names alias->bricks alias->test-bricks alias->bricks-to-test profile->bricks profile->test-bricks profile->bricks-to-test changed-components changed-bases thousand-sep) bricks)
        total-locs-src (map :total-lines-of-code-src environments)
        total-loc-row (->total-loc-row show-loc? profiles total-loc-src-bricks total-loc-test-bricks total-locs-src thousand-sep)
        rows (if show-loc? (conj brick-rows total-loc-row) brick-rows)
        header-colors (->header-colors show-loc? env-spc-cnt profile-spc-cnt)
        component-colors (mapv #(->brick-colors % env-spc-cnt profile-spc-cnt show-loc?) sorted-components)
        base-colors (mapv #(->brick-colors % env-spc-cnt profile-spc-cnt show-loc?) sorted-bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt profile-spc-cnt) :none))]
        row-colors (concat component-colors base-colors total-loc-colors)]
    (text-table/table "  " alignments header-colors header-orientations row-colors headers rows color-mode)))

(defn print-table [{:keys [settings interfaces components bases environments changes messages total-loc-src-bricks total-loc-test-bricks total-loc-src-environments total-loc-test-environments]} show-loc?]
  (let [{:keys [color-mode thousand-sep profile->settings]} settings
        {:keys [changed-components changed-bases env->bricks-to-test]} changes
        table (ws-table color-mode components bases environments profile->settings changed-components changed-bases env->bricks-to-test total-loc-src-bricks total-loc-test-bricks thousand-sep show-loc?)
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

(defn print-table-str-keys [workspace show-loc?]
  (print-table (walk/keywordize-keys workspace) show-loc?))
