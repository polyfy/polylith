(ns polylith.clj.core.validate.interfc
  (:require [polylith.clj.core.validate.m101-illegal-namespace-deps :as m101]
            [polylith.clj.core.validate.m102-duplicated-parameter-lists :as m102]
            [polylith.clj.core.validate.m103-missing-defs :as m103]
            [polylith.clj.core.validate.m104-circular-deps :as m104]
            [polylith.clj.core.validate.m105-illegal-name-sharing :as m105]
            [polylith.clj.core.validate.m106-multiple-interface-occurrences :as m106]
            [polylith.clj.core.validate.m107-missing-componens-in-environment :as m107]
            [polylith.clj.core.validate.m109-top-brick-namespace-clashes :as m109]
            [polylith.clj.core.validate.m201-mismatching-parameters :as m201]))

(defn messages [interface-names interfaces components bases environments top-namespaces color-mode]
  (vec (sort-by (juxt :type :code :message)
                (set (concat (m101/errors interface-names components bases color-mode)
                             (m102/errors components color-mode)
                             (m103/errors interfaces components color-mode)
                             (m104/errors interfaces components environments color-mode)
                             (m105/errors interface-names components bases color-mode)
                             (m106/errors components environments color-mode)
                             (m107/errors components bases environments color-mode)
                             (m109/errors components bases top-namespaces color-mode)
                             (m201/warnings interfaces components color-mode))))))
