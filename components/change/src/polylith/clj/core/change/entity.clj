(ns polylith.clj.core.change.entity
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn changed-entities [paths disk-paths]
  "Returns the bricks and environments that has changed based on a list of files"
  (let [path-entries (extract/path-entries [paths] disk-paths)]
    {:changed-bases (select/names path-entries c/base? c/exists?)
     :changed-components (select/names path-entries c/component? c/exists?)
     :changed-environments (select/names path-entries c/environment? c/exists?)}))
