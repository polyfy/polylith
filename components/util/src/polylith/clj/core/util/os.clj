(ns polylith.clj.core.util.os
  (:require [clojure.string :as str]))

(defn windows-os? []
  (str/includes? (-> "os.name" System/getProperty str/lower-case) "win"))
