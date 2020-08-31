(ns polylith.clj.core.deps.text-table.lib-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn lib [[name {:keys [mvn/version]}]]
  (when version [{:name name :version version}]))

(defn lib-cell [column row library]
  (text-table/cell column row library :none :left :horizontal))

(defn lib-column [libraries]
  (concat [(text-table/cell 1 1 "library" :none :left :horizontal)]
          (map-indexed #(lib-cell 1 (+ 3 %1) %2)
                       (map :name libraries))))

(defn version-column [libraries]
  (concat [(text-table/cell 3 1 "version" :none :left :horizontal)]
          (map-indexed #(lib-cell 3 (+ 3 %1) %2)
                       (map :version libraries))))

(defn flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? lib-deps lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn env-column [column {:keys [alias lib-deps]} libraries]
  (concat [(text-table/cell column 1 alias :purple :center :horizontal)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 (set (mapcat lib lib-deps)))
                       libraries)))

(defn env-columns [libraries environments]
  (apply concat (map-indexed #(env-column (+ 5 (* 2 %1)) %2 libraries)
                             environments)))

(defn profile-column [column libraries [profile {:keys [lib-deps]}]]
  (concat [(text-table/cell column 1 profile :purple :center :horizontal)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 (set (mapcat lib lib-deps)))
                       libraries)))

(defn profile-columns [column libraries profile->settings]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profile->settings)))

(defn brick-cell [column row lib-name lib-names]
  (let [flag (if (contains? (set lib-names) lib-name) "x" "-")]
    (text-table/cell column row flag :none :left :vertical)))

(defn brick-column [column {:keys [name type lib-dep-names]} lib-names]
  (concat [(text-table/cell column 1 name (type->color type) :right :vertical)]
          (map-indexed #(brick-cell column (+ 3 %1) %2 lib-dep-names)
                       lib-names)))

(defn brick-columns [column bricks lib-names]
  (apply concat (map-indexed #(brick-column (+ column (* 2 %1)) %2 lib-names)
                             bricks)))

(defn profile-lib [[_ {:keys [lib-deps]}]]
  (mapcat lib lib-deps))

(defn table [{:keys [settings components bases environments]}]
  (let [{:keys [profile->settings color-mode]} settings
        bricks (concat components bases)
        libraries (sort-by (juxt :name :version)
                           (set (concat (mapcat lib (mapcat :lib-deps environments))
                                        (mapcat profile-lib profile->settings))))
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        env-cols (env-columns libraries environments)
        profile-col (+ 5 (* 2 (count environments)))
        profile-cols (profile-columns profile-col libraries profile->settings)
        brick-col (+ profile-col (* 2 (count profile->settings)))
        lib-names (map :name libraries)
        n#envs (count environments)
        n#profiles (count profile->settings)
        n#bricks (count bricks)
        brick-cols (brick-columns brick-col bricks lib-names)
        space-columns (range 2 (* 2 (+ 2 n#envs n#profiles n#bricks)) 2)
        spaces (text-table/spaces 1 space-columns (repeat "  "))
        cells (text-table/merge-cells lib-col version-col env-cols profile-cols brick-cols spaces)
        line (text-table/line 2 cells)
        section2 (+ 2 (* 2 n#envs))
        section3 (+ 4 (* 2 (+ n#envs n#profiles)))
        sections (text-table/spaces 2 [4 section2 section3] (repeat "   "))]
    (text-table/table "  " color-mode cells line sections)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
