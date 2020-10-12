(ns polylith.clj.core.command.cmd-validator.executable
  (:require [polylith.clj.core.command.shared :as shared]
            [polylith.clj.core.command.message :as message]))

(defn can-be-executed-outside-ws? [workspace cmd entity]
  (and (nil? workspace)
       (or (nil? cmd)
           (= "help" cmd)
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

(defn validate [workspace cmd arg1 ws-dir ws-file is-search-for-ws-dir]
  (cond
    (can-be-executed-outside-ws? workspace cmd arg1) [true]
    (cant-execute-when-ws-file-is-set? cmd ws-file) [false (ws-file-error-message cmd)]
    (cant-execute-test? workspace cmd ws-dir is-search-for-ws-dir) [false (message/cant-be-executed-outside-ws-message cmd)]
    :else [true]))
