(ns polylith.workspace.text-table
  (:require [polylith.text-table.interface :as text-table]
            [polylith.common.interface.color :as color]))

(defn env-data [{:keys [alias component-names base-names]}]
  [alias (set (concat component-names base-names))])

(defn env-contains [alias brick alias->bricks]
  (if (contains? (alias->bricks alias) brick)
    "x"
    "-"))

(defn row [{:keys [name type interface lines-of-code-src lines-of-code-test]} aliases alias->bricks]
  (let [ifc (if (= "component" type)
              (:name interface)
              "-")
        loc-src (str lines-of-code-src)
        loc-test (str lines-of-code-test)
        all-env-contains (mapv #(env-contains % name alias->bricks) aliases)]
    (vec (concat [ifc "" name "" loc-src "" loc-test ""]
                 (interpose "" all-env-contains)))))

(def basic-headers ["interface" "  " "brick" "  " "loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left :right :left :right :left])

(defn brick-colors [color env-spc-cnt]
  (let [interface-color (if (= :blue color) :none :yellow)]
    (vec (concat [interface-color :none color :none :none :none :none :none]
                 (repeat env-spc-cnt :purple)))))

(defn print-table [{:keys [name components bases environments lines-of-code-src lines-of-code-test]}]
  (let [envs (filter (complement :test?) environments)
        aliases (mapv :alias envs)
        env-spc-cnt (inc (* (-> envs count dec) 2))
        alignments (concat basic-alignments (repeat env-spc-cnt :left))
        alias->bricks (into {} (map env-data envs))
        bricks (concat components bases)
        headers (concat basic-headers (interpose "  " aliases))
        rows (map #(row % aliases alias->bricks) bricks)
        header-colors (repeat (count headers) :none)
        component-colors (repeatedly (count components) #(brick-colors :green env-spc-cnt))
        base-colors (repeatedly (count bases) #(brick-colors :blue env-spc-cnt))
        all-colors (concat component-colors base-colors)
        table (text-table/table headers alignments rows header-colors all-colors)]
    (println "workspace: ")
    (println (str "  " name))
    (println)
    (println "lines of code, src (test):")
    (println (str "  " lines-of-code-src " (" lines-of-code-test ")"))
    (println)
    (println "environments:")
    (doseq [{:keys [alias name]} (filter (complement :test?) environments)]
      (println (str "  " alias " = " (color/purple name))))
    (println)
    (println table)))
