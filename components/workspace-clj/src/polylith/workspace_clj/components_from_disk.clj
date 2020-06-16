(ns polylith.workspace-clj.components-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.workspace-clj.imports-from-disk :as imports-from-disk]
            [polylith.workspace-clj.interface-defs-from-disk :as interface-defs-from-disk]))

(defn replace-underscore [string]
  (str/replace string "_" "-"))

(defn read-component [ws-path top-src-dir component-name]
  "Reads component from disk."
  (let [component-src-dir (str ws-path "/components/" component-name "/src/" top-src-dir)
        ; Only one folder should be in each components base src folder.
        ; The name of the folder will be the name of the interface,
        ; in case the component's name is not same as it's interface.
        interface-name (-> component-src-dir file/directory-paths first replace-underscore)
        src-dir (str component-src-dir interface-name)
        imports (imports-from-disk/all-imports component-src-dir)
        definitions (interface-defs-from-disk/defs-from-disk src-dir)]
    {:name component-name
     :type "component"
     :imports imports
     :interface {:name interface-name
                 :definitions definitions}}))

(defn read-components [ws-path top-src-dir component-names]
  "Reads components from disk."
  (vec (sort-by :name (map #(read-component ws-path top-src-dir %) component-names))))
