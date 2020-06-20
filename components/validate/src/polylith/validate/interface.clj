(ns polylith.validate.interface
  (:require [polylith.validate.illegal-namespace-deps :as illegal-namespace-deps]
            [polylith.validate.circular-deps :as circular-deps]
            [polylith.validate.illegal-name-sharing :as illegal-name-sharing]
            [polylith.validate.illegal-parameters :as illegal-parameters]
            [polylith.validate.missing-defs :as missing-defs]
            [polylith.validate.multiple-interface-occurrences :as multiple-ifcs]))

(defn warnings [interfaces components]
  (vec (sort (set (illegal-parameters/warnings interfaces components)))))

(defn errors [top-ns interface-names interfaces components bases environments]
  (vec (sort (set (concat (illegal-namespace-deps/errors top-ns interface-names components bases)
                          (circular-deps/errors interfaces)
                          (illegal-name-sharing/errors interface-names components bases)
                          (illegal-parameters/errors components)
                          (missing-defs/errors interfaces components)
                          (multiple-ifcs/errors components environments))))))
