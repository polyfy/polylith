(ns polylith.clj.core.test-helper.core
  (:require [clojure.string :as str]
            [clojure.stacktrace :as stacktrace]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.workspace.interfc :as ws]))

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

(defn execute-command [current-dir cmd arg1 arg2]
  (with-redefs [file/current-dir (fn [] (if (str/blank? current-dir)
                                          @root-dir
                                          (str @root-dir "/" current-dir)))
                git/current-sha (fn [_] "21f40507a24291ead2409ce33277378bb7e94ac6")]
    (let [ws-path (file/current-dir)
          workspace (read-workspace ws-path)
          {:keys [ok? system-error? exception]} (command/execute-command ws-path workspace cmd arg1 arg2)]
      (when (not ok?)
        (if system-error?
          (stacktrace/print-stack-trace exception)
          (ex/print-error-message exception))))))

(defn paths [dir]
  (let [paths (-> dir sub-dir file/relative-paths)]
    (set (filter #(not (str/starts-with? (str %) ".git/")) paths))))

(defn content [dir filename]
  (str/split-lines (slurp (str (sub-dir dir) "/" filename))))
