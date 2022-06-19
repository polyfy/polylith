(ns polylith.clj.core.common.config.read)

(defn substitute-if-alias
  "If a path in e.g. {:path ['src']} is a string, return the string
   (in a vector, so we can run mapcat), but if it's an existing alias,
   replace the path with the value of the alias."
  [source alias->path]
  (if (string? source)
    [source]
    (let [new-paths (alias->path source [source])]
      (if (sequential? new-paths)
        new-paths
        [new-paths]))))

(defn substitute-alias [sources alias->path]
  "If the incoming vector of sources are strings, e.g. ['src' 'resources'] then do nothing,
   but if it is an alias, e.g. [:src-paths :resources-paths], then return the substituted values
   taken from the alias->path map."
  (vec (mapcat #(substitute-if-alias % alias->path)
               sources)))

(defn substitute [content keys alias->path]
  (if-let [value (get-in content keys)]
    (assoc-in content keys (substitute-alias value alias->path))
    content))

(defn read-deps-file [deps-path]
  (let [content (-> deps-path slurp read-string)
        alias->path (:aliases content)]
    (-> content
        (substitute [:paths] alias->path)
        (substitute [:aliases :dev :extra-paths] alias->path)
        (substitute [:aliases :test :extra-paths] alias->path))))
