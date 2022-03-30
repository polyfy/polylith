(ns polylith.clj.core.test-runner.message
  (:refer-clojure :exclude [bases])
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn components [component-names color-mode]
  (when (seq component-names)
    [(color/component (str/join ", " component-names) color-mode)]))

(defn bases [base-names color-mode]
  (when (seq base-names)
    [(color/base (str/join ", " base-names) color-mode)]))
