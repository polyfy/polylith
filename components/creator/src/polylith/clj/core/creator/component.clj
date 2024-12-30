(ns ^:no-doc polylith.clj.core.creator.component
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-component [ws-dir settings component-name interface-name dialect is-git-add]
  (let [{:keys [top-namespace interface-ns]} settings
        components-dir (str ws-dir "/components/" component-name)]
    (brick/create-resources-dir ws-dir "components" component-name is-git-add)
    (brick/create-config-file ws-dir "components" component-name dialect is-git-add)
    (brick/create-src-ns ws-dir "components" top-namespace components-dir interface-ns interface-name dialect is-git-add)
    (brick/create-test-ns ws-dir "components" top-namespace components-dir interface-ns interface-name interface-name dialect is-git-add)))

(defn create [{:keys [ws-dir settings] :as workspace} component-name interface-name dialect is-git-add]
  (let [ifc-name (or interface-name component-name)]
    (brick/create-brick workspace
                        component-name
                        (fn [] (create-component ws-dir settings component-name ifc-name dialect is-git-add)))))
