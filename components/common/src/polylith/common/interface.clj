(ns polylith.common.interface
  (:require [polylith.common.core :as core]))

(defn all-bases
  ([ws-path]
   (core/all-bases ws-path nil))
  ([ws-path paths]
   (core/all-bases ws-path paths)))

(defn all-components
  ([ws-path]
   (core/all-components ws-path nil))
  ([ws-path paths]
   (core/all-components ws-path paths) []))

(defn create-print-channel []
  (core/create-print-channel))

(defn create-thread-pool [size]
  (core/create-thread-pool size))

(defmacro execute-in [pool & body]
  `(core/execute-in ~pool ~body))

(defn extract-source-paths
  ([ws-path deps service-or-env include-tests?]
   (core/extract-source-paths ws-path deps service-or-env include-tests?))
  ([ws-path deps service-or-env]
   (core/extract-source-paths ws-path deps service-or-env false)))

(defn make-classpath [libraries source-paths]
  (core/make-classpath libraries source-paths))

(defn resolve-libraries
  ([deps]
   (core/resolve-libraries deps nil false nil))
  ([deps service-or-env]
   (core/resolve-libraries deps service-or-env false nil))
  ([deps service-or-env include-tests?]
   (core/resolve-libraries deps service-or-env include-tests? nil))
  ([deps service-or-env include-tests? additional-deps]
   (core/resolve-libraries deps service-or-env include-tests? additional-deps)))

(defn run-in-jvm [classpath expression dir ex-msg]
  (core/run-in-jvm classpath expression dir ex-msg))