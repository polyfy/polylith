(ns polylith.clj.core.deps.brick-deps-table
  (:require [polylith.clj.core.deps.brick-deps :as brick-deps]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(def alignments [:left :center :left :center :left])

(defn add-empty-rows [rows max-rows]
  (let [cnt (- max-rows (count rows))]
    (concat rows (repeat cnt ["" :none]))))

(defn color-row [[[_ depender-color] [_ dependee-color]]]
  [depender-color :none :none :none dependee-color])

(defn table [{:keys [dependers dependees]} brick-name brick->color color-mode]
  (let [headers ["used by" "  <  " brick-name "  >  " "uses"]
        brick-color (brick->color brick-name)
        header-colors [:none :none brick-color :none :none]
        max-rows (max (count dependers) (count dependees))
        depender-rows (add-empty-rows dependers max-rows)
        dependee-rows (add-empty-rows dependees max-rows)
        row-colors (map color-row (map vector depender-rows dependee-rows))
        depender-name-rows (map first depender-rows)
        dependee-name-rows (map first dependee-rows)
        rows (map vector
                  depender-name-rows
                  (repeat "") (repeat "") (repeat "")
                  dependee-name-rows)]
    (text-table/table "  " alignments header-colors row-colors headers rows color-mode)))

(def type->color {"component" :green
                  "base" :blue})

(defn =env [{:keys [name alias]} env]
  (or (= env name)
      (= env alias)))

(defn print-table [{:keys [environments components bases]} environment-name brick-name color-mode]
  (let [environment (util/find-first #(=env % environment-name) environments)
        bricks (concat components bases)
        brick->color (into {} (map (juxt :name #(-> % :type type->color)) bricks))]
    (cond
      (nil? environment) (println (str "Couldn't find environment " (color/environment environment-name color-mode) "."))
      (-> brick-name brick->color nil?) (println (str "Couldn't find brick '" brick-name "'."))
      :else (let [brick->interface-deps (into {} (map (juxt :name :interface-deps) bricks))
                  deps (brick-deps/deps environment brick->color brick->interface-deps brick-name)]
              (println (table deps brick-name brick->color color-mode))))))
