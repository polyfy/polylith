(ns ^:no-doc polylith.clj.core.file.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.tools.deps :as tda]
            [clojure.tools.reader :refer [resolve-symbol]]
            [edamame.core :as edamame]
            [me.raynes.fs :as fs]
            [polylith.clj.core.util.interface.str :as str-util])
  (:import (clojure.lang ExceptionInfo)
           [java.io File FileNotFoundException]
           [java.nio.file Files]))

(defn file [^String f]
  (File. f))

(defn image-file? [filename]
  (or (str/ends-with? filename ".bmp")
      (str/ends-with? filename ".gif")
      (str/ends-with? filename ".jpeg")
      (str/ends-with? filename ".jpg")
      (str/ends-with? filename ".png")
      (str/ends-with? filename ".tif")
      (str/ends-with? filename ".tiff")
      (str/ends-with? filename ".wbmp")))

(defn execute-fn [f message path]
  (try
    (f)
    (catch Exception e
      (println (str "Warning. " message " '" path "': " (.getMessage e))))))

(defn size [path]
  (cond (fs/directory? path) (apply + (pmap #(or (size %) 0) (.listFiles (io/file path))))
        (fs/file? path) (fs/size path)
        :else nil))

(defn delete-file [path]
  (execute-fn #(io/delete-file path true)
              "Could not delete file" path))

(defn delete-dir [path]
  (doseq [f (reverse (file-seq (io/file path)))]
    (if (or (Files/isSymbolicLink (.toPath f)) (.exists f))
      (delete-file f))))

(defn exists [^String path]
  (if path
    (.exists (File. path))
    false))

(defn create-file
  ([path rows overwrite-if-exists?]
   (when (or overwrite-if-exists?
             (-> path exists not))
     (delete-file path)
     (doseq [row rows]
       (execute-fn
         #(spit path (str row "\n") :append true)
         "Could not create file" path)))))

(defn create-temp-dir [dir]
  (let [temp-file (execute-fn #(File/createTempFile dir "")
                              "Could not create directory in temp directory" dir)
        _         (.delete temp-file)
        _         (.mkdirs temp-file)
        path (.getPath temp-file)]
    (str-util/skip-if-ends-with path "/")))

(defn create-dir [^String path]
  (when (-> path exists not)
    (.mkdir (File. path))))

(defn absolute-path
  "The call to normalized will also clean up . and .. in the path."
  [path]
  (when path
    (-> path
        io/file
        .toPath
        fs/normalized
        .toString
        str-util/ensure-slash)))

(defn current-dir []
  (absolute-path ""))

(defn directory? [^File file]
  (.isDirectory file))

(defn copy-file-or-dir+ [from to]
  (if (-> from io/file directory?)
    (fs/copy-dir from to)
    (fs/copy+ from to)))

(defn filename [^File file]
  (-> file
      .getName
      str-util/ensure-slash))

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

(defn not-hidden? [path]
  (-> path file fs/hidden? not))

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

(defn source-reader [tag]
  (cond
    (= tag 'uuid) #(java.util.UUID/fromString %)
    (= tag 'inst) #(clojure.instant/read-instant-date %)
    :else (fn [data]
            {:unknown-tag tag
             :value data})))

(defn read-file [path]
  (try
    (let [content (slurp path)]
      (if (str/blank? content)
        ;; Return a special marker for empty files
        :polylith.clj.core.file.interface/empty-file
        (edamame/parse-string-all content
                                  {:fn true
                                   :var true
                                   :quote true
                                   :regex true
                                   :deref true
                                   :read-eval true
                                   :features #{:clj}
                                   :readers source-reader
                                   :read-cond :allow
                                   :auto-resolve name
                                   :auto-resolve-ns true
                                   :syntax-quote {:resolve-symbol resolve-symbol}})))
    (catch ExceptionInfo e
      (let [{:keys [row col]} (ex-data e)]
        (println (str "  Couldn't read file '" path "', row: " row ", column: " col ". Message: " (.getMessage e)))
        {:error true
         :file-path path
         :message (.getMessage e)
         :row row
         :col col}))
    (catch Exception e
      (println (str "  Error reading file '" path "': " (.getMessage e)))
      {:error true
       :file-path path
       :message (.getMessage e)})))

(defn copy-resource-file! [source target-path]
  (delete-file target-path)
  (let [resource-file (io/input-stream (io/resource source))
        target-file (io/file target-path)]
    (execute-fn #(io/copy resource-file target-file)
                "Could not copy resource file" target-path)))

(defn files [dir]
  (map str (.list (io/file dir))))

(defn files-and-dirs-recursively [dir]
  (drop-last (reverse (file-seq (io/file dir)))))

(defn visible-paths-recursively [dir]
  (into []
        (comp (filter (complement fs/hidden?))
              (map #(str-util/ensure-slash (str %))))
        (files-and-dirs-recursively dir)))

(defn paths-recursively [dir]
  (map #(str-util/ensure-slash (str %))
       (files-and-dirs-recursively dir)))

(defn relative-paths [path]
  (let [length (inc (count path))]
    (map #(subs % length)
         (paths-recursively path))))

(defn create-missing-dirs [filename]
  (io/make-parents filename))

(defn pretty-spit [filename collection]
  (spit (io/file filename)
        (with-out-str (pp/write collection :dispatch pp/code-dispatch))))
