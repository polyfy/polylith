(ns polylith.clj.core.migrator.dev-deps)

(defn recreate-config-file [ws-dir]
  (let [path (str ws-dir "/deps.edn")
        content (read-string (slurp path))
        new-content (dissoc content :polylith)]
    (spit path new-content)))
