(ns ^:no-doc polylith.clj.core.shell.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.shell.jline :as jline]
            [polylith.clj.core.shell.candidate.engine :as engine]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.tap.interface :as tap]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.color :as color])
  (:import [org.jline.reader EndOfFileException]
           [org.jline.reader UserInterruptException])
  (:refer-clojure :exclude [next]))

(def ws-dir (atom nil))
(def ws-file (atom nil))

(defn prompt []
  (let [prefix (cond @ws-dir "dir:"
                     @ws-file "file:"
                     :else "")]
    (str prefix (-> engine/ws deref :name) "$ ")))

(defn logo [fake-poly? color-mode]
  (str "                  _      _ + _   _\n"
       (color/grey color-mode "#####") "   _ __  ___| |_  _| |-| |_| |_\n"
       (color/green color-mode "#####") "  | '_ \\/ _ \\ | || | | |  _| ' \\\n"
       (color/blue color-mode "#####") "  | .__/\\___/_|\\_, |_|_|\\__|_||_|\n"
       "       |_|          |__/ " (common/version-name fake-poly?)))

(defn enhance [{:keys [is-local is-github] :as user-input} dir file local? github? branch]
  (cond-> (assoc user-input :is-shell true
                            :is-local (or local? is-local false)
                            :is-github (or github? is-github false))
          dir (assoc :ws-dir dir)
          file (assoc :ws-file file)
          branch (assoc :branch branch)))

(defn switch-ws [user-input dir file local? github? branch workspace-fn]
  (let [input (enhance user-input dir file local? github? branch)]
    (reset! ws-dir dir)
    (reset! ws-file file)
    (reset! engine/ws
            (workspace-fn input file))))

(defn execute-command [command-executor user-input local? github? branch color-mode]
  (try
    (let [input (enhance user-input @ws-dir @ws-file local? github? branch)]
      (command-executor input))
    (catch Throwable e
      (println (color/error color-mode (.getMessage e))))))

(defn current-branch
  "If we pass in :local, :github, or branch:BRANCH when starting a shell,
   then if we later execute the doc command, make sure we use the correct
   branch in GitHub if executing the command from the polylith repository
   in combination with :github."
  [cmd branch local? github? ws-dir]
  (or branch
      (when (and (or local? github?)
                 (= "doc" cmd))
        (if (git/is-polylith-repo? ws-dir)
          (git/current-branch)
          "master"))))

(defn start [command-executor {:keys [ws-dir ws-file is-local is-github branch is-tap is-fake-poly] :as user-input} workspace-fn workspace color-mode]
  (let [reader (jline/reader)
        shell-branch branch
        local? is-local
        github? is-github]
    (when is-tap
      (tap/execute "open"))
    (println (logo is-fake-poly color-mode))
    (reset! engine/ws workspace)
    (switch-ws user-input ws-dir ws-file is-local is-github branch workspace-fn)
    (tap> {:workspace @engine/ws})
    (try
      (loop []
        (flush)
        (when-let [line (.readLine reader (prompt))]
          (let [{:keys [branch cmd unnamed-args is-local is-github dir file color-mode] :as input} (user-input/extract-params (str-util/split-text line))
                color-mode (or color-mode (common/color-mode input))
                is-local (or is-local local?)
                is-github (or is-github github?)
                branch (or branch
                           (current-branch cmd shell-branch is-local is-github (or dir ws-dir (:ws-dir workspace))))]
            (when-not (contains? #{"exit" "quit"} cmd)
              (cond
                (= "shell" cmd) (println "  Can't start a shell inside another shell.")
                (= "switch-ws" cmd) (switch-ws input dir file is-local is-github branch workspace-fn)
                (= "tap" cmd) (tap/execute (first unnamed-args))
                (str/blank? line) nil
                :else (execute-command command-executor input is-local is-github branch color-mode))
              (flush)
              (recur)))))
      (catch EndOfFileException _)
      (catch UserInterruptException _))))
