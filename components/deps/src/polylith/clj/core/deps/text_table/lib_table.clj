(ns polylith.clj.core.deps.text-table.lib-table
  (:require [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.text-table.interface :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn lib [[name {:keys [version size]}]]
  (when version [{:name name
                  :version version
                  :size size}]))

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

(defn size-kb [{:keys [size]} thousand-sep]
  (if size
    (str-util/sep-1000 (quot size 1024) thousand-sep)
    ""))

(defn kb-cell [row library thousand-sep]
  (let [size (size-kb library thousand-sep)]
    (text-table/cell 5 row size :none :right :horizontal)))

(defn size-column [libraries thousand-sep]
  (concat [(text-table/cell 5 1 "KB" :none :right :horizontal)]
          (map-indexed #(kb-cell (+ 3 %1) %2 thousand-sep)
                       libraries)))

(defn flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? lib-deps lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn env-column [column {:keys [alias lib-deps]} libraries]
  (concat [(text-table/cell column 1 alias :purple :center :horizontal)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 (set (mapcat lib lib-deps)))
                       libraries)))

(defn env-columns [libraries environments]
  (apply concat (map-indexed #(env-column (+ 7 (* 2 %1)) %2 libraries)
                             environments)))

(defn profile-column [column libraries [profile {:keys [lib-deps]}]]
  (concat [(text-table/cell column 1 profile :purple :center :horizontal)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 (set (mapcat lib lib-deps)))
                       libraries)))

(defn profile-columns [column libraries profile-to-settings]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profile-to-settings)))

(defn brick-cell [column row lib-name lib-names empty-char]
  (let [flag (if (contains? (set lib-names) lib-name) "x" empty-char)]
    (text-table/cell column row flag :none :left :vertical)))

(defn brick-column [column {:keys [name type lib-dep-names]} lib-names empty-char]
  (concat [(text-table/cell column 1 name (type->color type) :right :vertical)]
          (map-indexed #(brick-cell column (+ 3 %1) %2 lib-dep-names empty-char)
                       lib-names)))

(defn brick-columns [column bricks lib-names empty-char]
  (apply concat (map-indexed #(brick-column (+ column (* 2 %1)) %2 lib-names empty-char)
                             bricks)))

(defn profile-lib [[_ {:keys [lib-deps]}]]
  (mapcat lib lib-deps))

(defn table [{:keys [settings components bases environments]}]
  (let [{:keys [profile-to-settings empty-char thousand-sep color-mode use-compact-output]} settings
        bricks (concat components bases)
        libraries (sort-by (juxt :name :version)
                           (set (concat (mapcat lib (mapcat :lib-deps environments))
                                        (mapcat profile-lib profile-to-settings))))
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        size-col (size-column libraries thousand-sep)
        env-cols (env-columns libraries environments)
        profile-col (+ 7 (* 2 (count environments)))
        profile-cols (profile-columns profile-col libraries profile-to-settings)
        brick-col (+ profile-col (* 2 (count profile-to-settings)))
        lib-names (map :name libraries)
        n#envs (count environments)
        n#profiles (count profile-to-settings)
        n#bricks (count bricks)
        brick-cols (brick-columns brick-col bricks lib-names empty-char)
        space-columns (range 2 (* 2 (+ 3 n#envs n#profiles n#bricks)) 2)
        space (if use-compact-output " " "  ")
        spaces (text-table/spaces 1 space-columns (repeat space))
        cells (text-table/merge-cells lib-col version-col size-col env-cols profile-cols brick-cols spaces)
        line (text-table/line 2 cells)
        section2 (+ 4 (* 2 n#envs))
        section3 (+ 6 (* 2 (+ n#envs n#profiles)))
        sections (text-table/spaces 2 [6 section2 section3] (repeat "   "))]
    (text-table/table "  " color-mode cells line sections)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
