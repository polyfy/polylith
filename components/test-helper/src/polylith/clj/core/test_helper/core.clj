(ns polylith.clj.core.test-helper.core
  (:require [clojure.string :as str]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.user-input.interface :as user-input]))

(def user-home "USER-HOME")

(def root-dir (atom nil))

(defn sub-dir [dir]
  (str @root-dir "/" dir))

(defn test-setup-and-tear-down [function]
  (let [path (file/create-temp-dir "polylith-root")]
    (if path
      (do
        (reset! root-dir path)
        (file/create-dir (sub-dir user-home)))
      (throw (Exception. "Could not create temp directory")))
    (function)
    (file/delete-dir path)))

(defn execute-command [current-dir args]
  (with-redefs [file/current-dir (fn [] (if (str/blank? current-dir)
                                          @root-dir
                                          (str @root-dir "/" current-dir)))
                user-config/home-dir (fn [] (str @root-dir "/" user-home))
                user-config/config-file-path
                (fn [] (str @root-dir "/" user-home "/.config/polylith/config.edn"))]
    (let [input (user-input/extract-params args)]
      (command/execute-command input))))

(defn paths [dir]
  (let [paths (-> dir sub-dir file/relative-paths)]
    (set (filter #(not (str/starts-with? (str %) ".git/")) paths))))

(defn content [dir filename]
  (str/split-lines (slurp (str (sub-dir dir) "/" filename))))
