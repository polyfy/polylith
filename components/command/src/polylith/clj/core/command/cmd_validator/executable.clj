(ns polylith.clj.core.command.cmd-validator.executable
  (:require [polylith.clj.core.command.shared :as shared]
            [polylith.clj.core.command.message :as message]))

(defn cant-be-executed-outside-ws? [workspace cmd [_ entity]]
  (not (or (-> workspace nil? not)
           (nil? cmd)
           (= "help" cmd)
           (= "version" cmd)
           (and (= "create" cmd)
                (= "w" (shared/entity->short entity))))))

(defn cant-execute-when-ws-file-is-set? [cmd ws-file]
  (and (contains? #{"create" "test"} cmd)
       (-> ws-file nil? not)))

(defn cant-execute-test? [workspace cmd ws-dir is-search-for-ws-dir]
  (and (= "test" cmd)
       (or (nil? workspace)
           is-search-for-ws-dir
           (-> ws-dir nil? not))))

(defn ws-file-error-message [cmd]
  (str "  The '" cmd "' command can't be executed when the workspace is read from file via 'ws-file'."))

(defn validate [workspace cmd args ws-dir ws-file is-search-for-ws-dir]
  (cond
    (cant-be-executed-outside-ws? workspace cmd args) [false (message/cant-be-executed-outside-ws-message cmd)]
    (cant-execute-when-ws-file-is-set? cmd ws-file) [false (ws-file-error-message cmd)]
    (cant-execute-test? workspace cmd ws-dir is-search-for-ws-dir) [false (message/cant-be-executed-outside-ws-message cmd)]
    :else [true]))
