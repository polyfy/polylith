(ns polylith.clj.core.validator.interfc
  (:require [polylith.clj.core.validator.m101-illegal-namespace-deps :as m101]
            [polylith.clj.core.validator.m102-function-or-macro-is-defined-twice :as m102]
            [polylith.clj.core.validator.m103-missing-defs :as m103]
            [polylith.clj.core.validator.m104-circular-deps :as m104]
            [polylith.clj.core.validator.m105-illegal-name-sharing :as m105]
            [polylith.clj.core.validator.m106-multiple-interface-occurrences :as m106]
            [polylith.clj.core.validator.m107-missing-componens-in-environment :as m107]
            [polylith.clj.core.validator.m108-environment-with-multi-implementing-component :as m108]
            [polylith.clj.core.validator.m201-mismatching-parameters :as m201]
            [polylith.clj.core.validator.m202-missing-libraries :as m202]
            [polylith.clj.core.validator.m203-invalid-src-reference :as m203]
            [polylith.clj.core.validator.m204-path-exists-in-both-dev-and-profile :as m204]
            [polylith.clj.core.validator.m205-lib-deps-exists-in-both-dev-and-profile :as m205]))

(defn messages [ws-dir suffixed-top-ns settings interface-names interfaces components bases environments interface-ns ns->lib color-mode]
  (vec (sort-by (juxt :type :code :message)
                (set (concat (m101/errors suffixed-top-ns interface-names components bases interface-ns color-mode)
                             (m102/errors components color-mode)
                             (m103/errors interfaces components color-mode)
                             (m104/errors environments color-mode)
                             (m105/errors interface-names components bases color-mode)
                             (m106/errors components environments color-mode)
                             (m107/errors components bases environments color-mode)
                             (m108/errors ws-dir interfaces environments color-mode)
                             (m201/warnings interfaces components color-mode)
                             (m202/warnings environments components bases ns->lib color-mode)
                             (m203/warnings environments color-mode)
                             (m204/warnings settings environments color-mode)
                             (m205/warnings settings environments color-mode))))))
