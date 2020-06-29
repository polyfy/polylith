(ns polylith.workspace-kotlin.indexedrows
  (:require [polylith.util.interface :as util]
            [clojure.string :as str]))

(defn starts-with? [[_ row] search-for]
  (str/starts-with? (str/trim row) search-for))

(defn row-contains? [[_ row] search-for]
  (-> (str/index-of row search-for)
      nil?
      not))

(defn find-first-that-starts-with
  ([indexed-rows search-for]
   (find-first-that-starts-with indexed-rows 0 search-for))
  ([indexed-rows start-index search-for]
   (util/find-first #(starts-with? % search-for)
                    (drop start-index indexed-rows))))

(defn find-first-that-contains [indexed-rows start-index search-for]
   (util/find-first #(row-contains? % search-for)
                    (drop start-index indexed-rows)))

(defn filter-matched
  ([indexed-rows search-for]
   (filterv #(starts-with? % search-for)
            indexed-rows))
  ([indexed-rows start-index end-index search-for]
   (filter-matched (subvec indexed-rows start-index end-index)
                   search-for)))
