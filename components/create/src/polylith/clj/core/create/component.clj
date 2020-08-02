(ns polylith.clj.core.create.component
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.git.interfc :as git]))

;(def top-namespace "polylith.clj.core")
;(def interface-ns "interfc")
;(def current-dir ".")
;(def component "abc")
;(def component-dir (str current-dir "/components/" component))
;
;(def top-dir (-> top-namespace common/sufix-ns-with-dot common/ns-to-path))
;
;(def src-interface-filename (str component-dir "/src/" top-dir component "/" interface-ns ".clj"))
;(file/create-missing-dirs src-interface-filename)
;(file/create-file src-interface-filename [(str "(ns " top-namespace "." component "." interface-ns ")")])
;(git/add "." src-interface-filename)

(defn create-resources-dir [current-dir component]
  (let [keep-file (str current-dir "/components/" component "/resources/" component "/.keep")]
    (file/create-missing-dirs keep-file)
    (file/create-file keep-file [""])
    (git/add "." keep-file)))

(defn create-src-interface [top-namespace component-dir top-dir interface-ns component-name]
  (let [ns-file (str component-dir "/src/" top-dir (common/ns-to-path component-name) "/" interface-ns ".clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." component-name "." interface-ns ")")])
    (git/add "." ns-file)))

(defn create-test-interface [top-namespace component-dir top-dir interface-ns component-name]
  (let [ns-file (str component-dir "/test/" top-dir (common/ns-to-path component-name) "/" interface-ns ".clj")]
    (file/create-missing-dirs ns-file)
    (file/create-file ns-file [(str "(ns " top-namespace "." component-name "." interface-ns)
                               (str "  (:require [clojure.test :refer :all]))")])
    (git/add "." ns-file)))

(defn create-component [current-dir settings component-name]
  (let [{:keys [top-namespace interface-ns]} settings
        top-dir (-> top-namespace common/sufix-ns-with-dot common/ns-to-path)
        components-dir (str current-dir "/components/" component-name)]
    (create-resources-dir current-dir component-name)
    (create-src-interface top-namespace components-dir top-dir interface-ns component-name)
    (create-test-interface top-namespace components-dir top-dir interface-ns component-name)))

(defn create [current-dir {:keys [settings components]} component-name]
  (if (common/find-component component-name components)
    (println (str "Component '" component-name "' already exists."))
    (create-component current-dir settings component-name)))
