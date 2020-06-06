(ns polylith.workspace.validate.validate
  (:require [polylith.workspace.validate.none-interface-deps :as none-interface-deps]
            [polylith.workspace.validate.circular-deps :as circular-deps]
            [polylith.workspace.validate.illegal-name-sharing :as illegal-name-sharing]
            [polylith.workspace.validate.incomplete-contracts :as incomplete-contracts]))

(defn warnings [interfaces components]
  (vec (sort (set (incomplete-contracts/warnings interfaces components)))))

(defn errors [top-ns interface-names interfaces components bases]
  (vec (sort (set (concat (none-interface-deps/errors top-ns interface-names components bases)
                          (circular-deps/errors interfaces)
                          (illegal-name-sharing/errors interface-names components bases)
                          (incomplete-contracts/errors components))))))
