(ns polylith.clj.core.shell.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.shell.jline :as jline]
            [polylith.clj.core.shell.candidate.engine :as engine]
            [polylith.clj.core.tap.interface :as tap]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.version.interface :as version])
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

(defn print-logo [color-mode]
  (println "                  _      _ + _   _");
  (println (str (color/grey color-mode "#####") "   _ __  ___| |_  _| |-| |_| |_"));
  (println (str (color/green color-mode "#####") "  | '_ \\/ _ \\ | || | | |  _| ' \\"));
  (println (str (color/blue color-mode "#####") "  | .__/\\___/_|\\_, |_|_|\\__|_||_|"));
  (println (str "       |_|          |__/ " version/name)))

(defn enhance [user-input dir file]
   (assoc user-input :is-shell true
                     :ws-dir dir
                     :ws-file file))

(defn switch-ws [user-input dir file workspace-fn]
  (let [input (enhance user-input dir file)]
    (reset! ws-dir dir)
    (reset! ws-file file)
    (reset! engine/ws
            (workspace-fn input file))))

(defn execute-command [command-executor user-input color-mode]
  (try
    (let [input (enhance user-input @ws-dir @ws-file)]
      (command-executor input))
    (catch Throwable e
      (println (color/error color-mode (.getMessage e))))))

(defn start [command-executor {:keys [ws-dir ws-file is-tap] :as user-input} workspace-fn workspace color-mode]
  (let [reader (jline/reader)]
    (when is-tap
      (tap/execute "open"))
    (print-logo color-mode)
    (reset! engine/ws workspace)
    (switch-ws user-input ws-dir ws-file workspace-fn)
    (tap> {:workspace @engine/ws})
    (try
      (loop []
        (flush)
        (when-let [line (.readLine reader (prompt))]
          (let [{:keys [cmd unnamed-args dir file color-mode] :as input} (user-input/extract-params (str-util/split-text line))
                color-mode (or color-mode (common/color-mode input))]
            (when-not (contains? #{"exit" "quit"} cmd)
              (cond
                (= "shell" cmd) (println "  Can't start a shell inside another shell.")
                (= "switch-ws" cmd) (switch-ws input dir file workspace-fn)
                (= "tap" cmd) (tap/execute (first unnamed-args))
                (str/blank? line) nil
                :else (execute-command command-executor input color-mode))
              (flush)
              (recur)))))
      (catch EndOfFileException _)
      (catch UserInterruptException _))))
