(ns ^:no-doc polylith.clj.core.creator.brick
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.creator.template :as template]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]
            [clojure.string :as str]))

(def create-brick-message
  (str "  Remember to add :local/root dependencies to dev and project 'deps.edn' files."))

(defn create-config-file [ws-dir bricks-dir brick-name is-git-add]
  (let [deps-filename (str ws-dir "/" bricks-dir "/" brick-name "/deps.edn")]
    (file/create-file deps-filename [(template/render ws-dir (str bricks-dir "/deps.edn") {})])
    (git/add ws-dir deps-filename is-git-add)))

(defn validate [brick-name workspace]
  (cond
    (str/blank? brick-name) [false "  A brick name must be given."]
    (common/find-brick brick-name workspace) [false (str "  The brick '" brick-name "' already exists.")]
    :else [true]))

(defn create-brick [workspace brick-name create-fn]
  (let [[ok? message] (validate brick-name workspace)]
    (if (not ok?)
      (println message)
      (do
        (create-fn)
        (println create-brick-message)))))

(defn create-resources-dir [ws-dir bricks-dir brick-name is-git-add]
  (let [keep-file (str ws-dir "/" bricks-dir "/" brick-name "/resources/" brick-name "/.keep")]
    (file/create-missing-dirs keep-file)
    (file/create-file keep-file [""])
    (git/add ws-dir keep-file is-git-add)))

(defn create-src-ns [ws-dir brick-type top-namespace bricks-dir namespace interface-name is-git-add]
  (let [top-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        ns-file (str bricks-dir "/src/" top-dir (common/ns-to-path interface-name) "/" (common/ns-to-path namespace) ".clj")
        ns-name (str top-namespace "." interface-name "." namespace)
        [template data]
        (if (= "bases" brick-type)
          ["bases/main.clj" {:main-ns ns-name}]
          ["components/interface.clj" {:interface-ns ns-name
                                       :impl-ns      (str top-namespace "." interface-name ".impl")}])]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(template/render ws-dir template data)])
    (when-let [opt-impl (and (= "components" brick-type)
                             (template/render ws-dir "components/impl.clj" data))]
      (file/create-file (str bricks-dir "/src/" top-dir
                             (common/ns-to-path interface-name) "/impl.clj")
                        [opt-impl]))
    (git/add ws-dir ns-file is-git-add)))

(defn create-test-ns [ws-dir brick-type top-namespace bricks-dir namespace entity-name alias is-git-add]
  (let [top-dir  (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
        ns-file  (str bricks-dir "/test/" top-dir (common/ns-to-path entity-name) "/" (common/ns-to-path namespace) "_test.clj")
        template (str brick-type "/test.clj")
        data     {:test-ns (str top-namespace "." entity-name "." namespace "-test")
                  :src-ns  (str top-namespace "." entity-name "." namespace)
                  :alias   alias}]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(template/render ws-dir template data)])
    (git/add ws-dir ns-file is-git-add)))
