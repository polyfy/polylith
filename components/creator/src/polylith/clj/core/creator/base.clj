(ns ^:no-doc polylith.clj.core.creator.base
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-base [ws-dir settings configs base-name dialect is-git-add]
  (let [{:keys [top-namespace]} settings
        bases-dir (str ws-dir "/bases/" base-name)
        template-data (-> configs :workspace :template-data)]
    (brick/create-resources-dir ws-dir "bases" base-name is-git-add)
    (brick/create-deps-config-file ws-dir "bases" base-name dialect is-git-add)
    (when (= "cljs" dialect)
      (brick/create-npm-config-file ws-dir template-data "bases" base-name dialect is-git-add))
    (brick/create-src-ns ws-dir "bases" top-namespace bases-dir "core" base-name dialect is-git-add)
    (brick/create-test-ns ws-dir "bases" top-namespace bases-dir "core" base-name "core" dialect is-git-add)))

(defn create [{:keys [ws-dir settings configs] :as workspace} base-name dialect is-git-add]
  (brick/create-brick workspace
                      base-name
                      (fn [] (create-base ws-dir settings configs base-name dialect is-git-add))))
