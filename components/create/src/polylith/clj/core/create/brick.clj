(ns polylith.clj.core.create.brick
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]))

(def create-brick-message "Remember to add the 'src', 'resources' and 'test' (if tests should be executed) directories to 'deps.edn' for desired environments.")

(defn create-brick [workspace brick-name create-fn]
  (if (common/find-brick workspace brick-name)
    (println (str "The brick '" brick-name "' already exists."))
    (do
      (create-fn)
      (println create-brick-message))))

(defn create-resources-dir [current-dir bricks-dir brick-name]
  (let [keep-file (str current-dir "/" bricks-dir "/" brick-name "/resources/" brick-name "/.keep")]
    (file/create-missing-dirs keep-file)
    (file/create-file keep-file [""])
    (git/add current-dir keep-file)))

(defn create-src-interface [current-dir top-namespace bricks-dir namespace brick-name]
  (let [top-dir (-> top-namespace common/sufix-ns-with-dot common/ns-to-path)
        ns-file (str bricks-dir "/src/" top-dir (common/ns-to-path brick-name) "/" namespace ".clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." brick-name "." namespace ")")])
    (git/add current-dir ns-file)))

(defn create-test-interface [current-dir top-namespace bricks-dir namespace brick-name]
  (let [top-dir (-> top-namespace common/sufix-ns-with-dot common/ns-to-path)
        ns-file (str bricks-dir "/test/" top-dir (common/ns-to-path brick-name) "/" namespace ".clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." brick-name "." namespace)
                               (str "  (:require [clojure.test :refer :all]))")])
    (git/add current-dir ns-file)))
