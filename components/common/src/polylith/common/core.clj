(ns polylith.common.core
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.common.aliases :as aliases]
            [polylith.common.read-components-from-disk :as componentsfromdisk]
            [polylith.common.readbasesfromdisk :as basesfromdisk]))

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


(defn top-namespace [{:keys [top-namespace]}]
  "Makes sure the top namespace ends with a dot (.) - if not empty."
  (if (str/blank? top-namespace)
    ""
    (if (str/ends-with? top-namespace ".")
      top-namespace
      (str top-namespace "."))))

(defn read-workspace-from-disk [ws-path {:keys [polylith] :as deps}]
  (let [top-ns (top-namespace polylith)
        top-src-dir (str/replace top-ns "." "/")
        component-names (file/directory-paths (str ws-path "/components"))
        components (componentsfromdisk/read-components-from-disk ws-path top-ns top-src-dir component-names)
        bases (basesfromdisk/read-bases-from-disk ws-path top-ns top-src-dir component-names)]
    {:polylith polylith
     :components components
     :bases bases
     :aliases (aliases/polylith-aliases deps)}))

;(componentsfromdisk/read-components-from-disk "." "polylith." "polylith/" (file/directory-paths "./components"))

;(read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}})
;
;(read-workspace-from-disk "../clojure-polylith-realworld-example-app" {:polylith {:top-namespace "clojure.realworld"}})
;(read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}})
