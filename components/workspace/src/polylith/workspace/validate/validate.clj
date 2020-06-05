(ns polylith.workspace.validate.validate
  (:require [polylith.workspace.validate.interface-deps :as interface-deps]
            [polylith.workspace.validate.circular-deps :as circular-deps]
            [polylith.workspace.validate.shared-names :as shared-names]
            [polylith.workspace.validate.incomplete-contracts :as incomplete-contracts]))

(defn warnings [interfaces components]
  (vec (sort (set (incomplete-contracts/warnings interfaces components)))))

(defn errors [top-ns interface-names components bases]
  (vec (sort (set (concat (interface-deps/errors top-ns interface-names components bases)
                          (circular-deps/errors interface-names components)
                          (shared-names/errors interface-names components bases)
                          (incomplete-contracts/errors components))))))
