(ns polylith.clj.core.shell.candidate.selector.file-explorer
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.user-config.interface :as user-config]))

(defn quotify [path]
  (if (str/index-of path " ")
    (str "\"" path "\"")
    path))

(defn dir-fn [value group select-fn]
  (let [dir (str (quotify value) "/")]
    (c/candidate dir
                 dir
                 (quotify value)
                 :fn [true
                      {:function select-fn
                       :group group}])))

(defn file-arg
  ([value group]
   (c/candidate (quotify value)
                value
                value :candidates [{:group (dissoc group :param)}])))

(defn with-current [args]
  (if (contains? #{".." "~"} (first args))
    args
    (concat ["."] args)))

(defn select [{:keys [group]} groups _]
  (let [{:keys [id param]} group
        args (get-in groups [id param :args])
        home-dir (user-config/home-dir)
        path (str/replace (str (str/join "/" (with-current args)) "/")
                          "\"" "")
        files-and-dirs (file/files-and-dirs path home-dir)
        prev-dir? (and (or (empty? args)
                           (= ".." (last args)))
                       (not= files-and-dirs
                             (file/files-and-dirs (str path "/..") home-dir)))
        {:keys [files dirs]} files-and-dirs]
    (vec (concat (map #(dir-fn % group #'select)
                      (cond-> dirs
                              prev-dir? (conj "..")
                              (empty? args) (conj "~")))
                 (map #(file-arg % group)
                      (filter #(str/ends-with? % ".edn") files))))))
