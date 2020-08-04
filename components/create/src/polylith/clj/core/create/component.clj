(ns polylith.clj.core.create.component
  (:require [polylith.clj.core.create.brick :as brick]))

(defn create-component [current-dir settings component-name interface-name]
  (let [{:keys [top-namespace interface-ns]} settings
        components-dir (str current-dir "/components/" component-name)]
    (brick/create-resources-dir current-dir "components" component-name)
    (brick/create-src-interface current-dir top-namespace components-dir interface-ns interface-name)
    (brick/create-test-interface current-dir top-namespace components-dir interface-ns interface-name)))

(defn create [current-dir {:keys [settings] :as workspace} component-name interface-name]
  (let [ifc-name (or interface-name component-name)]
    (brick/create-brick workspace
                        component-name
                        (fn [] (create-component current-dir settings component-name ifc-name)))))
