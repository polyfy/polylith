(ns polylith.clj.core.lib.git-size
  (:require [polylith.clj.core.file.interface :as file]))

(defn dir-size [dir]
  (when (file/exists dir)
    (apply + (map file/size (file/paths-recursively dir)))))

(defn with-size-and-version [lib-name sha value user-home]
  (let [path (str user-home "/.gitlibs/libs/" lib-name "/" sha)
        version (subs sha 0 7)]
    (if-let [size (dir-size path)]
      [lib-name (assoc value :type "git" :size size :version version)]
      [lib-name (assoc value :type "git" :version version)])))
