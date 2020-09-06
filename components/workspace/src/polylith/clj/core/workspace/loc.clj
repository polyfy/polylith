(ns polylith.clj.core.workspace.loc
  (:require [polylith.clj.core.file.interface :as file]))

(defn lines-of-code [namespaces]
  (apply + (mapv file/lines-of-code
                 (mapv :file-path namespaces))))
