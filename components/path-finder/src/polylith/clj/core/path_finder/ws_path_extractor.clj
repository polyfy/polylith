(ns ^:no-doc polylith.clj.core.path-finder.ws-path-extractor
  (:require [polylith.clj.core.path-finder.path-extractor :as extract]))

(defn full-brick-paths [type name brick-name->paths]
  (let [{:keys [src test]} (brick-name->paths name)]
    {:src (mapv #(str (clojure.core/name type) "s/" name "/" %) src)
     :test (mapv #(str (clojure.core/name type) "s/" name "/" %) test)}))

(defn path-entries [{:keys [alias type name]} alias->workspace]
  (let [{:keys [components bases paths]} (alias->workspace alias)
        brick-name->paths (into {} (map (juxt :name :paths)
                                        (concat components bases)))
        brick-paths (full-brick-paths type name brick-name->paths)]
    (map #(assoc % :alias alias)
         (extract/from-paths brick-paths paths))))

(defn lib-brick [[_ {:keys [brick]}]]
  brick)

(defn profile-bricks [{:keys [lib-deps]}]
  (filter identity (map lib-brick lib-deps)))

(defn profile-path-entries [profile alias->workspace]
  (mapcat #(path-entries % alias->workspace)
          (profile-bricks profile)))
