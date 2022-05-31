(ns polylith.clj.core.workspace-clj.brick-dirs)

(defn source-dirs [brick-dir top-src-dir paths]
  (mapv #(str brick-dir "/" % "/" top-src-dir)
        (filter #(not= "resources" %)
                paths)))
