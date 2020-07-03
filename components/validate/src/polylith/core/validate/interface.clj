(ns polylith.core.validate.interface
  (:require [polylith.core.validate.m101-illegal-namespace-deps :as m101]
            [polylith.core.validate.m102-duplicated-parameter-lists :as m102]
            [polylith.core.validate.m103-missing-defs :as m103]
            [polylith.core.validate.m104-circular-deps :as m104]
            [polylith.core.validate.m105-illegal-name-sharing :as m105]
            [polylith.core.validate.m106-multiple-interface-occurrences :as m106]
            [polylith.core.validate.m201-mismatching-parameters :as m201]
            [polylith.core.validate.m107-missing-componens-in-environment :as m107]))

(defn messages [top-ns interface-names interfaces components bases environments color-mode]
  (vec (sort-by (juxt :type :code :message)
                (set (concat (m101/errors top-ns interface-names components bases color-mode)
                             (m102/errors components color-mode)
                             (m103/errors interfaces components color-mode)
                             (m104/errors interfaces components environments color-mode)
                             (m105/errors interface-names components bases color-mode)
                             (m106/errors components environments color-mode)
                             (m107/errors components bases environments color-mode)
                             (m201/warnings interfaces components color-mode))))))
