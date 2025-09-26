(ns ^:no-doc polylith.clj.core.lib.size-npm
  (:require [polylith.clj.core.file.interface :as file]))

(defn with-size [[lib version] node-modules-path]
  (let [lib-name (-> lib name str)]
    [lib-name
     {:version version
      :type "npm"
      :size (file/directory-size (str node-modules-path "/" lib-name))}]))

(defn with-sizes-vec [ws-dir libraries]
  (let [node-module-path (str ws-dir "/node_modules")]
    (mapv #(with-size  % node-module-path)
          libraries)))
