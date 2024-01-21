(ns ^:no-doc polylith.clj.core.info.table.stable-since
  (:require [clojure.string :as str]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(defn short-name [sha]
  (if (>= (count sha) 7)
    (subs sha 0 7)
    sha))

(defn table [since-sha since-tag color-mode]
  (let [short-sha (short-name since-sha)
        stable-since (if (-> since-tag str/blank? not)
                       (text-table/cell 1 1 (str "stable since: " (color/grey color-mode (str short-sha " | " since-tag))))
                       (if (str/blank? since-sha)
                         (text-table/cell 1 1 (str (color/warning color-mode "Warning:") (color/error color-mode " not a git repo!")))
                         (text-table/cell 1 1 (str "stable since: " (color/grey color-mode short-sha)))))
        empty-line (text-table/empty-line 2)]
    (text-table/table "  " color-mode [stable-since empty-line])))