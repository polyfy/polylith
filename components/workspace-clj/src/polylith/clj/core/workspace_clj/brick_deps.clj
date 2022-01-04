(ns polylith.clj.core.workspace-clj.brick-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn brick-name [path is-dev]
  (let [prefix (if is-dev "" "../../")
        base-path (str prefix "bases/")
        component-path (str prefix "components/")]
    (when path
      (cond
        (str/starts-with? path base-path) (str-util/skip-prefix path base-path)
        (str/starts-with? path component-path) (str-util/skip-prefix path component-path)))))

(defn extract-brick-name
  "Returns the brick name from a dependency if it's a valid path to a brick."
  [[_ entry] is-dev]
  (let [path (:local/root entry)
        name (brick-name path is-dev)]
    (when name [name])))

(defn extract-brick-names [is-dev dependencies]
  (set (mapcat #(extract-brick-name % is-dev) dependencies)))
