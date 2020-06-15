(ns polylith.cmd.compile
  "Compile Clojure source into .class files."
  (:require [clojure.core.async :refer [>!!]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.workspace.interface :as ws])
  (:refer-clojure :exclude [compile])
  (:import (java.io File)))

(defn delete-folder [file]
  (let [files (reverse (file-seq file))]
    (doseq [^File f files]
      (when (.exists f)
        (io/delete-file f)))))

(defn ensure-compile-folder [compile-path]
  (let [compile-folder (io/file compile-path)]
    (delete-folder compile-folder)
    (.mkdir compile-folder)))

(defn file-path->ext [path]
  (let [last-dot  (str/last-index-of path ".")
        extension (if last-dot (subs path (inc last-dot)) "")]
    extension))

(def accepted-source-file-extensions #{"clj" "cljc"})

(defn file->compile-expr [src-path ^File file]
  (let [path (.getAbsolutePath file)
        ext  (file-path->ext path)]
    (when (and (.exists file)
               (not (.isDirectory file))
               (contains? accepted-source-file-extensions ext))
      (let [ns-str (-> path
                       (str/replace (str src-path "/") "")
                       (str/replace (str "." ext) "")
                       (str/replace "/" ".")
                       (str/replace "_" "-"))]
        (str "(clojure.core/compile (symbol \"" ns-str "\"))")))))

(defn compile-expressions [src-path]
  (->> src-path
       (io/file)
       (file-seq)
       (map #(file->compile-expr src-path %))
       (filter #(not (nil? %)))
       (str/join " ")))

(defn compilation-expr [compile-path src-path interface-expressions]
  (let [src (io/file src-path)]
    (when (or (not (.isDirectory src))
              (not (.exists src)))
      (throw (ex-info "Invalid source path." {:src-path src-path})))
    (let [expressions (str interface-expressions " " (compile-expressions src-path))]
      (str "(binding [*compile-path* \"" compile-path "\"]" expressions ")"))))

(defn compile-item [print-channel libraries ws-path compile-path interface-path interface-expressions item type]
  (let [sub-folder   (if (= :base type) "bases" "components")
        compile-path (str ws-path "/" sub-folder "/" item "/" compile-path)
        item-path    (str ws-path "/" sub-folder "/" item "/src")
        source-paths [compile-path interface-path item-path]
        classpath    (common/make-classpath libraries source-paths)
        expression   (compilation-expr compile-path item-path interface-expressions)]
    (when-not (ensure-compile-folder compile-path)
      (throw (ex-info "Could not create compile folder." {:ws-path      ws-path
                                                          :compile-path compile-path})))
    (>!! print-channel (str "-> Compiling " item))
    (common/run-in-jvm classpath expression ws-path "Exception during compilation.")
    (>!! print-channel (str "-> Compiled " item))))

(defn compile-base [print-channel libraries ws-path compile-path interface-path interface-expressions base]
  (compile-item print-channel libraries ws-path compile-path interface-path interface-expressions base :base))

(defn compile-component [print-channel libraries ws-path compile-path interface-path interface-expressions component]
  (compile-item print-channel libraries ws-path compile-path interface-path interface-expressions component :component))

(defn compile [{:keys [ws-path polylith] :as workspace} env]
  (let [start (. System (nanoTime))
        {:keys [compile-path thread-pool-size] :or {compile-path "target"}} polylith
        libraries (ws/resolve-libs workspace env)
        interface-path (str ws-path "/interfaces/src")
        interface-expressions (compile-expressions interface-path)
        paths (when env (ws/src-paths workspace env))
        _ (when (= [] paths)
            (throw (ex-info (str "No source paths found. Check service or environment name: " env)
                            {:env env})))
        all-bases (common/all-bases ws-path paths)
        all-components (common/all-components ws-path paths)
        print-channel (common/create-print-channel)
        thread-pool (common/create-thread-pool thread-pool-size)
        component-tasks (mapv #(common/execute-in thread-pool
                                                  (compile-component print-channel libraries ws-path compile-path interface-path interface-expressions %))
                              all-components)
        base-tasks (mapv #(common/execute-in thread-pool
                                             (compile-base print-channel libraries ws-path compile-path interface-path interface-expressions %))
                         all-bases)
        all-tasks (concat component-tasks base-tasks)]
    (mapv deref all-tasks)
    (>!! print-channel (str "\n-> Compiled " (count component-tasks) " components and " (count base-tasks) " bases in " (/ (double (- (. System (nanoTime)) start)) 1000000.0) " milliseconds."))
    (>!! print-channel :done)))
