(ns polylith.workspace-kotlin.components-from-disk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.workspace-kotlin.namespaces-from-disk :as ns-from-disk]
            [polylith.workspace-kotlin.interface-defs-from-disk :as defs-from-disk]))

;(def rows (str/split-lines (slurp "../kotlin-polylith/bases/tool/src/interface.kt")))
;(def rows (str/split-lines (slurp "../kotlin-polylith/components/user/src/interface.kt")))

(defn import? [line]
  (str/starts-with? (str/trim line) "import "))

(defn fun? [line]
  (str/starts-with? (str/trim line) "fun "))

(defn replace-underscore [string]
  (when string
    (str/replace string "_" "-")))

(defn read-component [src-path top-src-dir component-name]
  "Reads component from disk."
  (let [component-src-dir (str src-path "/components/" component-name "/src/" top-src-dir)
        component-test-dir (str src-path "/components/" component-name "/test/" top-src-dir)
        interface-name (-> component-src-dir file/directory-paths first replace-underscore)
        src-dir (str component-src-dir interface-name)
        namespaces-src (ns-from-disk/namespaces-from-disk component-src-dir)
        namespaces-test (ns-from-disk/namespaces-from-disk component-test-dir)]
        ;definitions (defs-from-disk/defs-from-disk src-dir)]
    {:src namespaces-src
     :tst namespaces-test}))

;(defn read-components [src-path top-src-dir component-names]
;  "Reads components from disk."
;  (vec (sort-by :name (map #(read-component src-path top-src-dir %) component-names))))
;
;(def src-path "../polylith-kotlin/kotlin")
;(def top-src-dir "polylith/kotlin/")
;(def component-names ["file" "srcreader"])
;
;(read-components src-path top-src-dir component-names)



;"/Users/tengstrand/source/polylith-kotlin/components/file/src/polylith/kotlin/file/core.kt"

;(def rows (str/split-lines (slurp "/Users/tengstrand/source/polylith-kotlin/clojure/components/workspace-kotlin/src/polylith/workspace_kotlin/read_code.clj")))

