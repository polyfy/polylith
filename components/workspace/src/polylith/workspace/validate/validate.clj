(ns polylith.workspace.validate.validate
  (:require [polylith.workspace.validate.interface-deps :as validate-deps]
            [polylith.workspace.validate.circular-deps :as validate-cdeps]
            [polylith.workspace.validate.shared-names :as validate-snames]
            [polylith.workspace.validate.contracts-defined :as validate-idefined]))

(defn errors [top-ns interface-names components bases]
  (vec (sort (concat (validate-deps/errors top-ns interface-names components bases)
                     (validate-cdeps/errors interface-names components)
                     (validate-snames/errors interface-names components bases)
                     (validate-idefined/errors components)))))
