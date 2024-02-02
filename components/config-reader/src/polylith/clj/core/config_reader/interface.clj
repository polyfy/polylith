(ns ^:no-doc polylith.clj.core.config-reader.interface
  (:require [polylith.clj.core.config-reader.check-file :as check-file]
            [polylith.clj.core.config-reader.config-reader :as config-reader]
            [polylith.clj.core.config-reader.deps-reader :as deps-reader]
            [polylith.clj.core.config-reader.ws-root :as ws-root]))

(defn file-exists?
  [filename _]
  "The second argument is used for test purposes."
  (check-file/file-exists? filename))

(defn clean-project-configs [configs]
  (config-reader/clean-project-configs configs))

(defn read-edn-file [file-path filename]
  (deps-reader/read-edn-file file-path filename))

(defn read-deps-file [file-path filename]
  (deps-reader/read-deps-file file-path filename))

(defn read-brick-config-files [ws-dir ws-type entity-type config-filename]
  (config-reader/read-or-use-default-brick-dep-files ws-dir ws-type entity-type config-filename))

(defn read-project-config-files [ws-dir ws-type config-filename]
  (config-reader/read-project-config-files ws-dir ws-type config-filename))

(defn read-development-deps-file [ws-dir ws-type]
  (config-reader/read-development-config-files ws-dir ws-type nil))

(defn read-workspace-config-file [ws-dir]
  (config-reader/read-workspace-config-file ws-dir))

(defn workspace-dir [user-input]
  (ws-root/workspace-dir user-input))
