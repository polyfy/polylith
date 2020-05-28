(ns polylith.core.oldstuff-interface)

;(defn all-bases-from-disk
;  ([ws-path paths]
;   (workspace/all-bases-from-disk ws-path paths))
;  ([ws-path]
;   (workspace/all-bases-from-disk ws-path)))
;
;(defn all-components-from-disk
;  ([ws-path paths]
;   (workspace/all-components-from-disk ws-path paths))
;  ([ws-path]
;   (workspace/all-components-from-disk ws-path)))
;
;(defmacro execute-in [pool & body]
;  `(core/execute-in ~pool ~body))
;
;(defn create-thread-pool [size]
;  (core/create-thread-pool size))
;
;(defn create-print-channel []
;  (core/create-print-channel))
;
;(defn extract-aliases
;  ([deps service-or-env include-tests?]
;   (core/extract-aliases deps service-or-env include-tests?))
;  ([deps service-or-env]
;   (core/extract-aliases deps service-or-env))
;  ([deps]
;   (core/extract-aliases deps)))
;
;(defn extract-extra-deps [deps service-or-env include-tests? additional-deps]
;  (core/extract-extra-deps deps service-or-env include-tests? additional-deps))
;
;(defn extract-source-paths
;  ([ws-path deps service-or-env include-tests?]
;   (core/extract-source-paths ws-path deps service-or-env include-tests?))
;  ([ws-path deps service-or-env]
;   (core/extract-source-paths ws-path deps service-or-env)))
;
;(defn resolve-libraries
;  ([deps service-or-env include-tests? additional-deps]
;   (core/resolve-libraries deps service-or-env include-tests? additional-deps))
;  ([deps service-or-env include-tests?]
;   (core/resolve-libraries deps service-or-env include-tests?))
;  ([deps service-or-env]
;   (core/resolve-libraries deps service-or-env))
;  ([deps]
;   (core/resolve-libraries deps)))
;
;(defn make-classpath [libraries source-paths]
;  (core/make-classpath libraries source-paths))
;
;(defn run-in-jvm [classpath expression dir ex-msg]
;  (core/run-in-jvm classpath expression dir ex-msg))
