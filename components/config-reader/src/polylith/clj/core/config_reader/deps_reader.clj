(ns ^:no-doc polylith.clj.core.config-reader.deps-reader
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io IOException)))

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

(defn substitute-alias
  "If the incoming vector of sources are strings, e.g. ['src' 'resources'] then do nothing,
   but if it is an alias, e.g. [:src-paths :resources-paths], then return the substituted values
   taken from the alias->path map."
  [sources alias->path]
  (vec (mapcat #(substitute-if-alias % alias->path)
               sources)))

(defn substitute [content keys alias->path]
  (if-let [value (get-in content keys)]
    (assoc-in content keys (substitute-alias value alias->path))
    content))

(defn read-edn-file
  "Load edn from an io/reader source (filename or io/resource)."
  [file-path filename]
  (try
    (with-open [reader (io/reader file-path)]
      {:config (edn/read (java.io.PushbackReader. reader))})
    (catch IOException e
      {:error (str "Couldn't open '" filename "': " (.getMessage e))})
    (catch RuntimeException e
      {:error (str "Couldn't parse '" filename "': " (.getMessage e))})))

(defn read-deps-file [file-path filename]
  (let [{:keys [config error]} (read-edn-file file-path filename)
        alias->path (:aliases config)]
    (cond-> {}
            config (assoc :config (-> config
                                      (substitute [:paths] alias->path)
                                      (substitute [:aliases :dev :extra-paths] alias->path)
                                      (substitute [:aliases :test :extra-paths] alias->path)))
            error (assoc :error error))))
