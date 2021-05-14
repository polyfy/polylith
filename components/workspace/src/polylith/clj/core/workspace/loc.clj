(ns polylith.clj.core.workspace.loc
  (:require [polylith.clj.core.file.interface :as file]))

(defn lines-of-code-source [namespaces]
  (apply + (mapv file/lines-of-code
                 (mapv :file-path namespaces))))

(defn lines-of-code [{:keys [src test]}]
  {:src (lines-of-code-source src)
   :test (lines-of-code-source test)})
