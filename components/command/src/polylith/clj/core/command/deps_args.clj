(ns polylith.clj.core.command.deps-args
  (:require [clojure.string :as str]))

(defn specified? [name]
  (and (not= "-" name)
       (-> name str/blank? not)))
