(ns ^:no-doc polylith.clj.core.file.core
  (:require [clojure.instant :as i]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.tools.reader :refer [resolve-symbol]]
            [edamame.core :as edamame]
            [me.raynes.fs :as fs]
            [polylith.clj.core.util.interface :as util]
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
  (doseq [^File f (reverse (file-seq (io/file path)))]
    (when (or (Files/isSymbolicLink (.toPath f)) (.exists f))
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
  (let [^File temp-file (execute-fn #(File/createTempFile dir "")
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
    (let [normalized-file (-> path
                              io/file
                              .toPath
                              fs/normalized)]
      (str-util/ensure-slash (.toString ^File normalized-file)))))

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
    (= tag 'inst) i/read-instant-date
    :else (fn [data]
            {:unknown-tag tag
             :value data})))

(defn- match-statements [statement splicing? features]
  (let [feature-statement-tuples (partition 2 2 [nil] statement)]
    (reduce (fn [acc [feature statement]]
              (if (and (contains? features feature) (some? statement))
                (if splicing?
                  (apply conj acc statement)
                  (conj acc statement))
                acc))
      []
      feature-statement-tuples)))

(defn- handle-reader-conditional [features parsed-statement]
  (let [splicing? (-> parsed-statement meta :edamame/read-cond-splicing)
        matched-statements (match-statements parsed-statement splicing? features)]
    (vary-meta matched-statements
               #(assoc % :edamame/read-cond-splicing true))))

(defn- parse-code-str* [code-str features read-cond]
  (edamame/parse-string-all code-str
                            {:fn true
                             :var true
                             :quote true
                             :regex true
                             :deref true
                             :symbol true
                             :read-eval true
                             :features features
                             :readers source-reader
                             :read-cond read-cond
                             :auto-resolve name
                             :auto-resolve-ns true
                             :syntax-quote {:resolve-symbol resolve-symbol}}))

(defn ns-with-name? [content]
  (and (sequential? content)
       (= (symbol "ns")
          (first content))
       (-> content second boolean)))

(defn- clear-ns-statements [code features]
  (reduce (fn [acc statement]
            (if (ns-with-name? statement)
              (let [code (parse-code-str* (str statement) features
                           (partial handle-reader-conditional features))]
                (conj acc (first code)))
              (conj acc statement)))
    []
    code))

(defn parse-code-str [code-str dialects]
  (let [features (->> dialects (map keyword) (into #{}))
        multi-dialect? (< 1 (count features))]
    (if multi-dialect?
      (let [code (parse-code-str* code-str features :preserve)]
        (clear-ns-statements code features))
      (parse-code-str* code-str features :allow))))

(defn read-file [path dialects]
  (try
    (let [content (slurp path)]
      (if (str/blank? content)
        ;; Return a special marker for empty files
        :polylith.clj.core.file.interface/empty-file
        (parse-code-str content dialects)))
    (catch ExceptionInfo e
      (let [{:keys [row col]} (ex-data e)]
        {:error true
         :file-path path
         :message (.getMessage e)
         :row row
         :col col}))
    (catch Exception e
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
  (let [normalized-collection (util/sanitize-keywords collection)]
    (spit (io/file filename)
          (with-out-str (pp/write normalized-collection :dispatch pp/code-dispatch)))))

(defn directory-size [^File path]
  (cond
    (nil? path) 0
    (not (.exists path)) 0
    (.isFile path) (.length path)
    :else (reduce + (map directory-size (.listFiles path)))))
