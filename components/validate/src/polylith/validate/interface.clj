(ns polylith.validate.interface
  (:require [polylith.validate.illegal-namespace-deps :as illegal-namespace-deps]
            [polylith.validate.circular-deps :as circular-deps]
            [polylith.validate.illegal-name-sharing :as illegal-name-sharing]
            [polylith.validate.illegal-parameters :as illegal-parameters]
            [polylith.validate.missing-data :as missing-data]
            [polylith.validate.missing-functions-and-macros :as missing-fn]))

(defn warnings [interfaces components]
  (vec (sort (set (illegal-parameters/warnings interfaces components)))))

(defn errors [top-ns interface-names interfaces components bases]
  (vec (sort (set (concat (illegal-namespace-deps/errors top-ns interface-names components bases)
                          (circular-deps/errors interfaces)
                          (illegal-name-sharing/errors interface-names components bases)
                          (illegal-parameters/errors components)
                          (missing-data/errors interfaces components)
                          (missing-fn/errors interfaces components))))))
