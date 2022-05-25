(ns polylith.clj.core.lib.git-size
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]))

(defn dir-size [root-path sha]
  (when sha
    (file/size (str root-path "/" sha))))

(defn sha-version [sha]
  (when sha
    (if (>= (count sha) 7)
      (subs sha 0 7)
      sha)))

(defn full-sha [sha path user-home]
  (when sha
    (util/find-first #(str/starts-with? % sha)
                     (:dirs (file/files-and-dirs path user-home)))))

(defn root-path [user-home lib-name]
  (str user-home "/.gitlibs/libs/" lib-name))

(defn with-size-and-version [lib-name sha coords user-home]
  (let [path (root-path user-home lib-name)
        full-sha (full-sha sha path user-home)
        version (sha-version sha)
        size (dir-size path full-sha)]
    [lib-name (cond-> (assoc coords :type "git" :version version)
                      size (assoc :size size))]))
