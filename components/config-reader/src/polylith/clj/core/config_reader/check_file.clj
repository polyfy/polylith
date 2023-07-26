(ns ^:no-doc polylith.clj.core.config-reader.check-file
  (:require [polylith.clj.core.file.interface :as file]))

(defn file-exists? [filename]
  (-> filename file/exists))
