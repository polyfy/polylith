(ns polylith.clj.core.workspace-clj.brick-dirs)

(defn source-dirs [brick-dir paths]
  (mapv #(str brick-dir "/" %)
        (filter #(not= "resources" %)
                paths)))

(defn top-source-dirs [brick-dir top-src-dir paths]
  (mapv #(str % "/" top-src-dir)
        (source-dirs brick-dir paths)))
