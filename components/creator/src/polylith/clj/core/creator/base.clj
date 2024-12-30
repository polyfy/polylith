(ns ^:no-doc polylith.clj.core.creator.base
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-base [ws-dir settings base-name dialect is-git-add]
  (let [{:keys [top-namespace]} settings
        bases-dir (str ws-dir "/bases/" base-name)]
    (brick/create-resources-dir ws-dir "bases" base-name is-git-add)
    (brick/create-config-file ws-dir "bases" base-name is-git-add)
    (brick/create-src-ns ws-dir "bases" top-namespace bases-dir "core" base-name dialect is-git-add)
    (brick/create-test-ns ws-dir "bases" top-namespace bases-dir "core" base-name "core" dialect is-git-add)))

(defn create [{:keys [ws-dir settings] :as workspace} base-name dialect is-git-add]
  (brick/create-brick workspace
                      base-name
                      (fn [] (create-base ws-dir settings base-name dialect is-git-add))))
