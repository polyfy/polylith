(ns polylith.clj.core.lib.size
  (:require [polylith.clj.core.lib.git-size :as git-size]
            [polylith.clj.core.lib.mvn-size :as mvn-size]
            [polylith.clj.core.lib.local-size :as local-size]))

(defn destruct [{sha1 :sha :keys [git/sha local/root mvn/version]}]
  {:sha (or sha sha1)
   :root root
   :version version})

(defn with-size [ws-dir [name coords] entity-root-path user-home]
  (let [{:keys [sha version root]} (destruct coords)
        [lib-name lib] (cond
                         version (mvn-size/with-size name version coords)
                         root (local-size/with-size-and-version ws-dir name root coords entity-root-path)
                         :else (git-size/with-size-and-version name sha coords user-home))]
    [(str lib-name) lib]))

(defn with-sizes-vec [ws-dir entity-root-path libraries user-home]
  (mapv #(with-size ws-dir % entity-root-path user-home)
       libraries))
