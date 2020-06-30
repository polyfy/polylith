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

(defn component-colors [{:keys [name]} aliases alias->bricks]
  (vec (concat [:yellow :none :green :none :none :none :none :none]
               (env-colors aliases name alias->bricks))))

(defn base-colors [{:keys [name]} aliases alias->bricks]
  (vec (concat [:none :none :blue :none :none :none :none :none]
               (env-colors aliases name alias->bricks))))

(defn print-table [{:keys [name components bases environments lines-of-code-src lines-of-code-test]}]
  (let [envs (filter (complement :test?) environments)
        aliases (mapv :alias envs)
        env-spc-cnt (inc (* (-> envs count dec) 2))
        alignments (concat basic-alignments (repeat env-spc-cnt :left))
        alias->bricks (into {} (map env-data envs))
        bricks (concat components bases)
        headers (concat basic-headers (interpose "  " aliases))
        brick-rows (mapv #(row % aliases alias->bricks) bricks)
        total-loc-row (total-loc-row env-spc-cnt lines-of-code-src lines-of-code-test)
        rows (conj brick-rows total-loc-row)
        header-colors (repeat (count headers) :none)
        component-colors (mapv #(component-colors % aliases alias->bricks) components)
        base-colors (mapv #(base-colors % aliases alias->bricks) bases)
        total-loc-colors [(vec (repeat (+ 8 env-spc-cnt) :none))]
        all-colors (concat component-colors base-colors total-loc-colors)
        table (text-table/table headers alignments rows header-colors all-colors)]
    (println "workspace: ")
    (println (str "  " name))
    (println)
    (println "environments:")
    (doseq [{:keys [alias name]} (filter (complement :test?) environments)]
      (println (str "  " alias " = " (color/purple name))))
    (println)
    (println table)))
