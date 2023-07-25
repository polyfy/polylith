(ns ^:no-doc polylith.clj.core.workspace-clj.brick-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn brick-name [path is-dev]
  (when path
    (when-let [brick-path (util/find-first #(str/starts-with? path %)
                                           (if is-dev ["bases/" "components/" "./bases/" "./components/"]
                                                      ["../../bases/" "../../components/"]))]
      (str-util/skip-prefix path brick-path))))

(defn extract-brick-name
  "Returns the brick name from a dependency if it's a valid path to a brick."
  [[_ entry] is-dev]
  (let [path (:local/root entry)
        name (brick-name path is-dev)]
    (when name [name])))

(defn extract-brick-names [is-dev dependencies]
  (set (mapcat #(extract-brick-name % is-dev) dependencies)))
