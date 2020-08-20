(ns polylith.clj.core.path-finder.interfc.lib-dep-extract
  (:require [polylith.clj.core.path-finder.lib-dep-extractor :as lib-dep-extractor]))

(defn lib-deps-entries [dev? src-deps test-deps settings]
  (lib-dep-extractor/lib-dep-entries dev? src-deps test-deps settings))
