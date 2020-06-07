(ns polylith.workspace.validate.validate
  (:require [polylith.workspace.validate.illegal-namespace-deps :as illegal-namespace-deps]
            [polylith.workspace.validate.circular-deps :as circular-deps]
            [polylith.workspace.validate.illegal-name-sharing :as illegal-name-sharing]
            [polylith.workspace.validate.illegal-signatures :as illegal-signatures]
            [polylith.workspace.validate.missing-functions-and-macros :as missing]))

(defn warnings [interfaces components]
  (vec (sort (set (illegal-signatures/warnings interfaces components)))))

(defn errors [top-ns interface-names interfaces components bases]
  (vec (sort (set (concat (illegal-namespace-deps/errors top-ns interface-names components bases)
                          (circular-deps/errors interfaces)
                          (illegal-name-sharing/errors interface-names components bases)
                          (illegal-signatures/errors components)
                          (missing/errors interfaces components))))))
