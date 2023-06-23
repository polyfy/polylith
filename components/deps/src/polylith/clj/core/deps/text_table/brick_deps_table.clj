(ns polylith.clj.core.deps.text-table.brick-deps-table
  (:require [polylith.clj.core.deps.text-table.shared :as shared]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn interface-cell [row interface-name]
  (text-table/cell 1 row interface-name :yellow :left :horizontal))

(defn interface-column [interface-names]
  (concat
    [(text-table/cell 1 1 "uses" :none :left :horizontal)]
    (map-indexed #(interface-cell (+ %1 3) %2)
                 interface-names)))

(defn deps? [interface-name deps context]
  (-> (filter #(= interface-name %) (context deps))
      empty? not))

(defn used-by-interface [{:keys [name type interface-deps]} interface-name]
  (cond (deps? interface-name interface-deps :src)  [[name (shared/type->color type)]]
        (deps? interface-name interface-deps :test) [[(str name " (t)") (shared/type->color type)]]))

(defn ifc [interface src-interfaces]
  (if (contains? src-interfaces interface)
    [interface :yellow]
    [(str interface " (t)") :yellow]))

(defn base [base src-bases]
  (if (contains? src-bases base)
    [base :blue]
    [(str base " (t)") :blue]))

(defn uses-ifc-base [{:keys [interface-deps base-deps]}]
  (let [interface-deps-src (set (:src interface-deps))
        interface-deps-test (:test interface-deps)
        base-deps-src (set (:src base-deps))
        base-deps-test (set (:test base-deps))]
    (concat (map #(ifc % interface-deps-src)
                 (sort (set (concat interface-deps-src interface-deps-test))))
            (map #(base % base-deps-src)
                 (sort (set (concat base-deps-src base-deps-test)))))))

(defn table [{:keys [components bases settings]} brick]
  (let [color-mode (:color-mode settings)
        brick-interface-name (-> brick :interface :name)
        bricks (concat components bases)
        uses (uses-ifc-base brick)
        uses-column (shared/deps-column 9 "uses" uses)
        used-by (mapcat #(used-by-interface % brick-interface-name) bricks)
        used-by-column (shared/deps-column 1 "used by" used-by)
        headers (shared/brick-headers brick color-mode)
        spaces (text-table/spaces 1 [2 4 6 8] (repeat "  "))]
    (text-table/table "  " color-mode used-by-column uses-column headers spaces)))

(defn print-table [workspace brick-name]
  (let [brick (common/find-brick brick-name workspace)]
    (if brick
      (text-table/print-table (table workspace brick))
      (println (str "  Couldn't find brick '" brick-name "'.")))))

(comment
  (require '[dev.jocke :as dev])
  (def workspace dev/workspace)
  (print-table workspace "deps")
  #__)
