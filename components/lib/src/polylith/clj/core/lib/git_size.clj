(ns polylith.clj.core.lib.git-size
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]))

(defn dir-size [root-path sha]
  (let [dir (str root-path "/" sha)]
    (when (and sha
               (file/exists dir))
      (apply + (map file/size (file/paths-recursively dir))))))

(defn sha-version [sha]
  (when sha
    (if (>= (count sha) 7)
      (subs sha 0 7)
      sha)))

(defn full-sha [sha root-path user-home]
  (util/find-first #(str/starts-with? % sha)
                   (:dirs (file/files-and-dirs root-path user-home))))

(defn with-size-and-version [lib-name sha value user-home]
  (let [root-path (str user-home "/.gitlibs/libs/" lib-name)
        full-sha (full-sha sha root-path user-home)
        version (sha-version sha)]
    (if-let [size (dir-size root-path full-sha)]
      [lib-name (assoc value :type "git" :size size :version version)]
      [lib-name (assoc value :type "git" :version version)])))
