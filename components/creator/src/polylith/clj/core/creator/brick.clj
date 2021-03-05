(ns polylith.clj.core.creator.brick
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]))

(def create-brick-message "  Remember to add src, resources and test directories to 'deps.edn' files.")

(defn create-config-file [ws-dir bricks-dir brick-name]
  (let [config-file (str ws-dir "/" bricks-dir "/" brick-name "/deps.edn")]
    (file/create-file config-file [(str "{:paths [\"src\"]")
                                   (str " :deps {}")
                                   (str " :aliases {:test {:extra-paths [\"test\"]")
                                   (str "                  :extra-deps []}}}")])))

(defn create-brick [workspace brick-name create-fn]
  (if (common/find-brick brick-name workspace)
    (println (str "  The brick '" brick-name "' already exists."))
    (do
      (create-fn)
      (println create-brick-message))))

(defn create-resources-dir [ws-dir bricks-dir brick-name]
  (let [keep-file (str ws-dir "/" bricks-dir "/" brick-name "/resources/" brick-name "/.keep")]
    (file/create-missing-dirs keep-file)
    (file/create-file keep-file [""])
    (git/add ws-dir keep-file)))

(defn create-src-ns [ws-dir top-namespace bricks-dir namespace interface-name]
  (let [top-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        ns-file (str bricks-dir "/src/" top-dir (common/ns-to-path interface-name) "/" namespace ".clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." interface-name "." namespace ")")])
    (git/add ws-dir ns-file)))

(defn create-test-ns [ws-dir top-namespace bricks-dir namespace interface-name]
  (let [top-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        ns-file (str bricks-dir "/test/" top-dir (common/ns-to-path interface-name) "/" namespace "_test.clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." interface-name "." namespace "-test")
                               (str "  (:require [clojure.test :refer :all]))")
                               ""
                               (str "(deftest dummy-test")
                               (str "  (is (= 1 1)))")])
    (git/add ws-dir ns-file)))
