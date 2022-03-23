(ns polylith.clj.core.validator.core
  (:require [polylith.clj.core.validator.m101-illegal-namespace-deps :as m101]
            [polylith.clj.core.validator.m102-function-or-macro-is-defined-twice :as m102]
            [polylith.clj.core.validator.m103-missing-defs :as m103]
            [polylith.clj.core.validator.m104-circular-deps :as m104]
            [polylith.clj.core.validator.m105-illegal-name-sharing :as m105]
            [polylith.clj.core.validator.m106-multiple-interface-occurrences :as m106]
            [polylith.clj.core.validator.m107-missing-componens-in-project :as m107]
            [polylith.clj.core.validator.m108-project-with-multi-implementing-component :as m108]
            [polylith.clj.core.validator.m201-mismatching-parameters :as m201]
            [polylith.clj.core.validator.m202-missing-paths :as m202]
            [polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile :as m203]
            [polylith.clj.core.validator.m205-non-top-namespace :as m205]))

(defn has-errors? [messages]
  (-> (filter #(= "error" (:type %)) messages) empty? not))

(defn validate-ws [suffixed-top-ns settings paths interface-names interfaces components bases projects interface-ns {:keys [cmd]} color-mode]
  (vec (sort-by (juxt :type :code :message)
                (set (concat (m101/errors suffixed-top-ns interface-names components bases interface-ns color-mode)
                             (m102/errors components color-mode)
                             (m103/errors interfaces components color-mode)
                             (m104/errors projects color-mode)
                             (m105/errors interface-names components bases color-mode)
                             (m106/errors components projects color-mode)
                             (m107/errors cmd settings projects components color-mode)
                             (m108/errors interfaces projects paths color-mode)
                             (m201/warnings interfaces components color-mode)
                             (m202/warnings projects paths color-mode)
                             (m203/warnings settings projects color-mode)
                             (m205/warnings components bases color-mode))))))
