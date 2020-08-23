(ns polylith.clj.core.workspace.text-table.lib-version-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.workspace.text-table.shared :as shared]))

(defn lib [[name {:keys [mvn/version]}]]
  (when version [{:name name :version version}]))

(defn lib-cell [column row library]
  (text-table/cell column row library :none :left :horizontal))

(defn lib-column [libraries]
  (concat [(shared/standard-cell "library" 1 1)]
          (map-indexed #(lib-cell 1 (+ 3 %1) %2)
                       (map :name libraries))))

(defn version-column [libraries]
  (concat [(shared/standard-cell "version" 3 1)]
          (map-indexed #(lib-cell 3 (+ 3 %1) %2)
                       (map :version libraries))))

(defn flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? (set (mapcat lib lib-deps)) lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn env-column [column {:keys [alias lib-deps]} libraries]
  (concat [(shared/standard-cell alias column 1 :purple :center)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 lib-deps)
                       libraries)))

(defn env-columns [libraries environments]
  (apply concat (map-indexed #(env-column (+ 5 (* 2 %1)) %2 libraries)
                             environments)))

(defn profile-column [column libraries [profile lib-deps]]
  (concat [(shared/standard-cell profile column 1 :purple :center)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 lib-deps)
                       libraries)))

(defn profile-columns [column libraries profile->settings]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profile->settings)))

(defn table [{:keys [settings environments]}]
  (let [{:keys [profile->settings color-mode]} settings
        libraries (sort-by (juxt :name :version) (set (mapcat lib (mapcat :lib-deps environments))))
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        env-cols (env-columns libraries environments)
        profile-col (+ 5 (* 2 (count environments)))
        profile-cols (profile-columns profile-col libraries profile->settings)
        space-columns (range 2 (* 2 (+ 2 (count environments) (count profile->settings))) 2)
        spaces (text-table/spaces 1 space-columns (repeat "  "))
        cells (text-table/merge-cells lib-col version-col env-cols profile-cols spaces)
        line (text-table/line 2 cells)
        sections (if (-> profile->settings count zero?) [4] [4 (+ 4 (* 2 (count environments)))])
        line-space (text-table/spaces 2 sections (repeat "   "))]
    (text-table/table "  " color-mode cells line line-space)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
