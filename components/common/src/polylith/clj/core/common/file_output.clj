(ns ^:no-doc polylith.clj.core.common.file-output
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.image-creator.interface :as image-creator]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn print-or-save-table [workspace table-fn canvas-areas post-print-fn]
  (let [filename (-> workspace :user-input :out)
        color-mode (-> workspace :settings :color-mode)
        table (table-fn workspace)]
    (if filename
      (if (file/image-file? filename)
        (image-creator/create-image filename table canvas-areas color-mode)
        (file/create-file filename table))
      (text-table/print-table table))
    (when post-print-fn
      (post-print-fn))))
