(ns polylith.common.core
  (:require [polylith.common.readfromdisk :as disk]
            [polylith.common.aliases :as aliases]))

;
;(def alias-nses #{"service" "env"})
;(def alias-nses-with-tests (into alias-nses #{"service.test" "env.test"}))
;
;(def number-of-processors
;  (memoize
;    (fn []
;      (max (int (/ (.availableProcessors (Runtime/getRuntime)) 2)) 1))))
;
;(defmacro execute-in [pool body]
;  "Executes the body in a separate thread with using the given thread pool."
;  `(.submit ^ExecutorService ~pool
;            ^Callable (fn [] ~@body)))
;
;(defn ^ExecutorService create-thread-pool [size]
;  (Executors/newFixedThreadPool (or size (number-of-processors))))
;
;(defn create-print-channel []
;  (let [ch (chan 1)]
;    (go-loop []
;      (let [message (<! ch)]
;        (if (= :done message)
;          (do
;            (close! ch))
;          (do
;            (println message)
;            (recur)))))
;    ch))
;
;(defn extract-aliases
;  ([deps service-or-env include-tests?]
;   (let [polylith-nses (if include-tests? alias-nses-with-tests alias-nses)
;         alias-keys    (when service-or-env (into #{} (map #(keyword % service-or-env) polylith-nses)))
;         aliases       (filter #(if alias-keys
;                                  (contains? alias-keys (key %))
;                                  (contains? polylith-nses (-> % key namespace)))
;                               (:aliases deps))]
;     (doall aliases)))
;  ([deps service-or-env]
;   (extract-aliases deps service-or-env false))
;  ([deps]
;   (extract-aliases deps nil)))
;
;(defn extract-extra-deps [deps service-or-env include-tests? additional-deps]
;  (let [aliases     (extract-aliases deps service-or-env include-tests?)
;        alias-deps  (map #(-> % val (select-keys [:extra-deps])) aliases)
;        merged-deps (apply merge-with merge alias-deps)]
;    (merge additional-deps
;           (:extra-deps merged-deps))))
;
;(defn extract-source-paths
;  ([ws-path deps service-or-env include-tests?]
;   (let [aliases        (extract-aliases deps service-or-env include-tests?)
;         paths          (into #{} (mapcat #(-> % val :extra-paths) aliases))
;         absolute-paths (mapv #(str ws-path (if (str/starts-with? % "/") "" "/") %) paths)]
;     absolute-paths))
;  ([ws-path deps service-or-env]
;   (extract-source-paths ws-path deps service-or-env false)))
;
;(defn resolve-libraries
;  ([{:keys [mvn/repos] :as deps} service-or-env include-tests? additional-deps]
;   (let [mvn-repos  (merge mvn/standard-repos repos)
;         deps       (assoc deps :mvn/repos mvn-repos)
;         extra-deps (extract-extra-deps deps service-or-env include-tests? additional-deps)]
;     (tools-deps/resolve-deps deps {:extra-deps extra-deps})))
;  ([deps service-or-env include-tests?]
;   (resolve-libraries deps service-or-env include-tests? nil))
;  ([deps service-or-env]
;   (resolve-libraries deps service-or-env false nil))
;  ([deps]
;   (resolve-libraries deps nil false nil)))
;
;(defn make-classpath [libraries source-paths]
;  (tools-deps/make-classpath libraries source-paths nil))
;
;(defn run-in-jvm [classpath expression dir ex-msg]
;  (let [{:keys [exit err out]} (shell/sh "java" "-cp" classpath "clojure.main" "-e" expression :dir dir)]
;    (if (= 0 exit)
;      out
;      (throw (ex-info ex-msg {:err err :exit-code exit})))))

(defn read-workspace-from-disk [ws-path {:keys [polylith] :as deps}]
  (let [top-namespace (:top-namespace polylith)
        {:keys [components bases]} (disk/read-components-and-bases-from-disk ws-path top-namespace)]
    {:polylith   polylith
     :components components
     :bases      bases
     :aliases    (aliases/polylith-aliases deps)}))

;(read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}})
;(read-workspace-from-disk "../clojure-polylith-realworld-example-app" {:polylith {:top-namespace "clojure.realworld"}})




;(def deps (-> "/Users/furkan/Workspace/clojure-polylith-realworld-example-app/deps.edn" slurp read-string))
;
;(with-open [writer (io/writer (io/file "polylith.edn"))]
;  (clojure.pprint/pprint (create-polylith-map "/Users/furkan/Workspace/clojure-polylith-realworld-example-app" deps) writer))
;
;(common/extract-aliases deps)
;(polylith-aliases deps)
;(name :a/bb)



;(def ws-path ".")
;(def component-name "common")
;(def base-src-folder "polylith")
;;
;(def component-base-src-folder (str ws-path "/components/" component-name "/src/" base-src-folder))
;(def interface-name (first (file/directory-names component-base-src-folder)))
;(def component-src-folder (str component-base-src-folder "/" interface-name))
;(def interface-file-content (file/read-file (str component-src-folder "/interface.clj")))
;(def declarations (filter-declarations interface-file-content))
;(def declarations-infos (vec (sort-by (juxt :type :name) (map ->declarations-info declarations))))
;
;(pp/pprint interface-file-content)
;
;(map str (pfile/files-recursively "./components/common"))


;"./components/common/src/polylith/common"

