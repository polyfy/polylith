(ns polylith.validate.interface
  (:require [polylith.validate.illegal-namespace-deps :as illegal-namespace-deps]
            [polylith.validate.circular-deps :as circular-deps]
            [polylith.validate.illegal-name-sharing :as illegal-name-sharing]
            [polylith.validate.mismatching-parameters :as mismatching-parameters]
            [polylith.validate.missing-defs :as missing-defs]
            [polylith.validate.multiple-interface-occurrences :as multiple-ifcs]))

(defn messages [top-ns interface-names interfaces components bases environments]
  (vec (sort-by (juxt :type :code :message)
                (set (concat (mismatching-parameters/warnings interfaces components)
                             (illegal-namespace-deps/errors top-ns interface-names components bases)
                             (circular-deps/errors interfaces components environments)
                             (illegal-name-sharing/errors interface-names components bases)
                             (mismatching-parameters/errors components)
                             (missing-defs/errors interfaces components)
                             (multiple-ifcs/errors components environments))))))
