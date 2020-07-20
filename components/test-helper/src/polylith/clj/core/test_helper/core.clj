(ns polylith.clj.core.test-helper.core
  (:require [clojure.string :as str]
            [clojure.stacktrace :as stacktrace]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.change.interfc :as change]))

(def root-dir (atom nil))

(defn sub-dir [dir]
  (str @root-dir "/" dir))

(defn test-setup-and-tear-down [function]
  (let [path (file/create-temp-dir "polylith-root")]
    (if path
      (reset! root-dir path)
      (throw (Exception. (str "Could not create directory: " path))))
    (function)
    (file/delete-dir path)))

(defn read-workspace [ws-path]
  (let [exists? (file/exists (str ws-path "/deps.edn"))]
    (when exists? (-> ws-path
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes))))

(defn execute-command [current-dir cmd arg1 arg2 arg3]
  (with-redefs [file/current-path (fn [] (str @root-dir "/" current-dir))]
    (let [ws-path (file/current-path)
          workspace (read-workspace ws-path)
          {:keys [ok? system-error? exception]} (command/execute-command ws-path workspace cmd arg1 arg2 arg3)]
      (when (not ok?)
        (if system-error?
          (stacktrace/print-stack-trace exception)
          (ex/print-error-message exception))))))

(defn paths [dir]
  (let [paths (-> dir sub-dir file/relative-paths)]
    (set (filter #(not (str/starts-with? (str %) ".git/")) paths))))

(defn content [dir filename]
  (file/read-file (str (sub-dir dir) "/" filename)))
