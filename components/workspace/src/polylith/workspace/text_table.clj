(ns polylith.workspace.text-table
  (:require [clojure.string :as str]
            [polylith.text-table.interface :as text-table]
            [polylith.util.interface.str :as str-util]))

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
    (concat [ifc "" name "" loc-src "" loc-test ""]
            (interpose "" all-env-contains))))

(def basic-headers ["interface" "  " "brick" "  " "loc" " " "(t)" "   "])
(def basic-alignments [:left :left :left :left :right :left :right :left])

(defn print-table [{:keys [name components bases environments lines-of-code-src lines-of-code-test]}]
  (let [envs (filter (complement :test?) environments)
        delimiter-rows (+ 2 (count components))
        aliases (mapv :alias envs)
        extra-alignments (inc (* (dec (count aliases)) 2))
        alignments (concat basic-alignments (repeat extra-alignments :left))
        alias->bricks (into {} (map env-data envs))
        bricks (concat components bases)
        headers (concat basic-headers (interpose "  " aliases))
        rows (map #(row % aliases alias->bricks) bricks)
        table-rows (text-table/table-rows headers alignments rows)
        first-table (str/join "\n" (take delimiter-rows table-rows))
        last-table (str/join "\n" (drop delimiter-rows table-rows))]
    (println "workspace: ")
    (println (str "  " name))
    (println)
    (println "lines of code, src (test):")
    (println (str "  " lines-of-code-src " (" lines-of-code-test ")"))
    (println)
    (println "environments:")
    (doseq [{:keys [alias name]} (filter (complement :test?) environments)]
      (println (str "  " alias " = " name)))
    (println)
    (println first-table)
    (println (str-util/line (-> table-rows first count)))
    (println last-table)))
