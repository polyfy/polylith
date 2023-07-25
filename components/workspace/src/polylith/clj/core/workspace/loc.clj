(ns ^:no-doc polylith.clj.core.workspace.loc
  (:require [polylith.clj.core.file.interface :as file]))

(defn lines-of-code-source [ws-dir namespaces]
  (apply + (mapv #(file/lines-of-code (str ws-dir "/" %))
                 (mapv :file-path namespaces))))

(defn lines-of-code [ws-dir {:keys [src test]}]
  {:src (lines-of-code-source ws-dir src)
   :test (lines-of-code-source ws-dir test)})
