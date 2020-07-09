(ns polylith.clj.core.workspace.loc
  (:require [polylith.clj.core.file.interfc :as file]))

(defn brick-loc [namespaces]
  (apply + (mapv file/lines-of-code
                 (mapv :file-path namespaces))))
