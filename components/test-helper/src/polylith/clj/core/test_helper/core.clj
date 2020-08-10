(ns polylith.clj.core.test-helper.core
  (:require [clojure.string :as str]
            [clojure.stacktrace :as stacktrace]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.workspace.interfc :as ws]))

(def user-home "USER-HOME")

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

(defn read-workspace [ws-dir args]
  (let [exists? (file/exists (str ws-dir "/deps.edn"))
        test-settings (common/test-settings args)]
    (when exists? (-> ws-dir
                      ws-clj/workspace-from-disk
                      (ws/enrich-workspace test-settings)
                      (change/with-changes test-settings)))))

(defn execute-command [current-dir cmd arg1 arg2 arg3]
  (with-redefs [file/current-dir (fn [] (if (str/blank? current-dir)
                                          @root-dir
                                          (str @root-dir "/" current-dir)))
                git/current-sha (fn [_] "21f40507a24291ead2409ce33277378bb7e94ac6")
                user-config/home-dir (fn [] (str @root-dir "/" user-home))]
    (let [ws-dir (file/current-dir)
          workspace (read-workspace ws-dir [arg1 arg2 arg3])
          {:keys [exception]} (command/execute-command ws-dir workspace cmd arg1 arg2 arg3)]
      (when (-> exception nil? not)
        (stacktrace/print-stack-trace exception)))))

(defn paths [dir]
  (let [paths (-> dir sub-dir file/relative-paths)]
    (set (filter #(not (str/starts-with? (str %) ".git/")) paths))))

(defn content [dir filename]
  (str/split-lines (slurp (str (sub-dir dir) "/" filename))))
