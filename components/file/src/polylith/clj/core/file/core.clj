(ns polylith.clj.core.file.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.tools.deps.alpha :as tda]
            [me.raynes.fs :as fs]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util])
  (:import [java.io File PushbackReader FileNotFoundException]
           [java.nio.file Files]))

(defn execute-fn [f message path]
  (try
    (f)
    (catch Exception e
      (println (str "Warning. " message " '" path "': " (.getMessage e))))))

(defn size [path]
  (cond (fs/directory? path) (apply + (pmap size (.listFiles (io/file path))))
        (fs/file? path) (fs/size path)
        :else 0))

(defn delete-file [path]
  (execute-fn #(io/delete-file path true)
              "Could not delete file" path))

(defn delete-dir [path]
  (doseq [f (reverse (file-seq (io/file path)))]
    (if (or (Files/isSymbolicLink (.toPath f)) (.exists f))
      (delete-file f))))

(defn create-file [path rows]
  (delete-file path)
  (doseq [row rows]
    (execute-fn
      #(spit path (str row "\n") :append true)
      "Could not create file" path)))

(defn create-temp-dir [dir]
  (let [temp-file (execute-fn #(File/createTempFile dir "")
                              "Could not create directory in temp directory" dir)
        _         (.delete temp-file)
        _         (.mkdirs temp-file)
        path (.getPath temp-file)]
    (str-util/skip-if-ends-with path "/")))

(defn create-dir [^String path]
  (.mkdir (File. path)))

(defn exists [^String path]
  (if path
    (.exists (File. path))
    false))

(defn absolute-path [path]
  (-> path io/file .getAbsolutePath))

(defn current-dir []
  (absolute-path ""))

(defn directory? [^File file]
  (.isDirectory file))

(defn copy-file-or-dir+ [from to]
  (if (-> from io/file directory?)
    (fs/copy-dir from to)
    (fs/copy+ from to)))

(defn filename [^File file]
  (.getName file))

(defn lines-of-code [file-path]
  (try
    (with-open [rdr (io/reader file-path)]
      (count (line-seq rdr)))
    (catch FileNotFoundException _
      0)))

(defn directory-paths [dir]
  (->> dir
       (io/file)
       (.listFiles)
       (filter directory?)
       (mapv filename)
       (into #{})))

(defn not-hidden? [name]
  (not (str/starts-with? name ".")))

(defn dirs-and-files [directory home-dir]
  (let [dir (str/replace directory "~/" (str home-dir "/"))
        result (group-by first
                         (map (juxt directory? filename)
                              (->> dir
                                   (io/file)
                                   (.listFiles))))
        files (filterv not-hidden? (map second (get result false)))
        dirs (filterv not-hidden? (map second (get result true)))]
    {:files files
     :dirs dirs}))

(defn read-file [path]
  (try
    (with-open [rdr (-> path
                        (io/reader)
                        (PushbackReader.))]
      (doall
        (take-while #(not= ::done %)
                    (repeatedly #(try (read rdr)
                                      (catch Exception _ ::done))))))
    (catch FileNotFoundException _
      nil)))

(defn copy-resource-file! [source target-path]
  (delete-file target-path)
  (let [resource-file (io/input-stream (io/resource source))
        target-file (io/file target-path)]
    (execute-fn #(io/copy resource-file target-file)
                "Could not copy resource file" target-path)))

(defn delete-folder [file]
  (let [files (reverse (file-seq file))]
    (doseq [^File f files]
      (when (.exists f)
        (io/delete-file f)))))

(defn files [dir]
  (map str (.list (io/file dir))))

(defn files-recursively [dir]
  (drop-last (reverse (file-seq (io/file dir)))))

(defn paths-recursively [dir]
  (map str (files-recursively dir)))

(defn relative-paths [path]
  (let [length (inc (count path))]
    (map #(str (subs % length))
         (map str (paths-recursively path)))))

(defn create-missing-dirs [filename]
  (io/make-parents filename))

(defn pretty-spit [filename collection]
  (spit (io/file filename)
        (with-out-str (pp/write collection :dispatch pp/code-dispatch))))

(defn file [^String f]
  (File. f))

(defn read-deps-file [path]
  (tda/slurp-deps (file path)))
