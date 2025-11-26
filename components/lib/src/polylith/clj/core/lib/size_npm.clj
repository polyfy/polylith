(ns ^:no-doc polylith.clj.core.lib.size-npm
  (:require [polylith.clj.core.file.interface :as file]))

(defn- keyword->full-name
  "Convert a keyword to its full string name, preserving namespace for scoped npm packages.
   For :@mantine/core, returns '@mantine/core' instead of just 'core'."
  [kw]
  (if-let [ns (namespace kw)]
    (str ns "/" (name kw))
    (name kw)))

(defn- find-package-dir
  "Find the npm package directory, checking workspace nested path first, then hoisted root.
   With npm/yarn workspaces, dependencies may be nested under the workspace package
   (e.g., node_modules/@poly/mantine/node_modules/@mantine/core) or hoisted to root."
  [ws-dir package-name lib-name]
  (let [nested-path (when package-name
                      (str ws-dir "/node_modules/" package-name "/node_modules/" lib-name))
        hoisted-path (str ws-dir "/node_modules/" lib-name)]
    (cond
      (and nested-path (file/exists nested-path)) nested-path
      (file/exists hoisted-path) hoisted-path
      :else hoisted-path)))

(defn- with-size [[lib version] ws-dir package-name]
  (let [lib-name (keyword->full-name lib)
        pkg-dir (find-package-dir ws-dir package-name lib-name)]
    [lib-name
     {:version version
      :type "npm"
      :size (file/directory-size pkg-dir)}]))

(defn with-sizes-vec [ws-dir package-name libraries]
  (mapv #(with-size % ws-dir package-name)
        libraries))
