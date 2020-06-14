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

(defn make-classpath [libraries source-paths]
  (core/make-classpath libraries source-paths))

(defn resolve-libraries
  ([workspace]
   (core/resolve-libraries workspace nil false nil))
  ([workspace env]
   (core/resolve-libraries workspace env false nil))
  ([workspace env include-tests?]
   (core/resolve-libraries workspace env include-tests? nil))
  ([workspace env include-tests? additional-deps]
   (core/resolve-libraries workspace env include-tests? additional-deps)))

(defn run-in-jvm [classpath expression dir ex-msg]
  (core/run-in-jvm classpath expression dir ex-msg))
