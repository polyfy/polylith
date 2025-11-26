(ns ^:no-doc polylith.clj.core.lib.size-npm
  (:require [polylith.clj.core.file.interface :as file]))

(defn- keyword->full-name
  "Convert a keyword to its full string name, preserving namespace for scoped npm packages.
   For :@mantine/core, returns '@mantine/core' instead of just 'core'."
  [kw]
  (if-let [ns (namespace kw)]
    (str ns "/" (name kw))
    (name kw)))

(defn with-size [[lib version] node-modules-path]
  (let [lib-name (keyword->full-name lib)]
    [lib-name
     {:version version
      :type "npm"
      :size (file/directory-size (str node-modules-path "/" lib-name))}]))

(defn with-sizes-vec [ws-dir libraries]
  (let [node-module-path (str ws-dir "/node_modules")]
    (mapv #(with-size  % node-module-path)
          libraries)))
