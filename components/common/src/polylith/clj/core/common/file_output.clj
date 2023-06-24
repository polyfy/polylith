(ns polylith.clj.core.common.file-output
  (:require [polylith.clj.core.image-creator.interface :as image-creator]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn print-or-save-table [workspace table-fn canvas-areas post-print-fn]
  (let [image (-> workspace :user-input :out)
        table (table-fn workspace)]
    (if image
      (image-creator/create-image image table canvas-areas)
      (do
        (text-table/print-table table)
        (when post-print-fn
          (post-print-fn))))))
