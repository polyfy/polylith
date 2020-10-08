(ns polylith.clj.core.lib.git-size
  (:require [polylith.clj.core.file.interface :as file]))

(defn dir-size [dir]
  (when (file/exists dir)
    (apply + (map file/size (file/paths-recursively dir)))))

(defn sha-version [sha]
  (when sha
    (if (>= (count sha) 7)
      (subs sha 0 7)
      sha)))

(defn with-size-and-version [lib-name sha value user-home]
  (let [path (str user-home "/.gitlibs/libs/" lib-name "/" sha)
        version (sha-version sha)]
    (if-let [size (dir-size path)]
      [lib-name (assoc value :type "git" :size size :version version)]
      [lib-name (assoc value :type "git" :version version)])))
