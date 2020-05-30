(ns polylith.workspace.validate
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [polylith.core.circulardeps :as circular]))

(defn circular-deps-messages [interfaces components]
  (let [circular-deps (circular/circular-deps interfaces components)]
    (if (nil? circular-deps)
      []
      [(str "Circular dependencies: " (str/join " > " circular-deps))])))

(defn shared-bricks-messages [interface-names components bases]
  (let [component-names (set (map :name components))
        component-and-interface-names (set/union interface-names component-names)
        base-names (set (map :name bases))
        shared-names (vec (set/intersection component-and-interface-names base-names))]
    (if (empty? shared-names)
      []
      [(str "Bases can't share names with interfaces or components: " shared-names)])))

(defn error-messages [interface-names components bases]
  (let [circular-deps (circular-deps-messages interface-names components)
        shared-bricks (shared-bricks-messages interface-names components bases)]
    {:errors (vec (concat circular-deps shared-bricks))}))
