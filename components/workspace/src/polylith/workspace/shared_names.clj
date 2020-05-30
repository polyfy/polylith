(ns polylith.workspace.shared-names
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn errors [interface-names components bases]
  (let [component-names (set (map :name components))
        component-and-interface-names (set/union (set interface-names) component-names)
        base-names (set (map :name bases))
        shared-names (str/join ", " (sort (set/intersection component-and-interface-names base-names)))]
    (if (empty? shared-names)
      []
      [(str "Bases can't share names with interfaces or components: " shared-names)])))