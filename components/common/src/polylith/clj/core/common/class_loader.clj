(ns polylith.clj.core.common.class-loader
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color])
  (:import (java.net URLClassLoader URL)))

(def base-classloader
  (or (.getClassLoader clojure.java.api.Clojure)
      (.getContextClassLoader (Thread/currentThread))))

(def ext-classloader
  (.getParent ^ClassLoader base-classloader))

(defn- url-classloader [url-array ext]
  (URLClassLoader.
    url-array
    ext))

(defmacro with-classloader [class-loader & body]
  `(binding [*use-context-classloader* false]
     (let [cl# (.getContextClassLoader (Thread/currentThread))]
       (try (.setContextClassLoader (Thread/currentThread) ~class-loader)
            ~@body
            (finally
              (.setContextClassLoader (Thread/currentThread) cl#))))))

(defn invoke-in* [^ClassLoader class-loader class-name method & [signature & params]]
  (let [class (.loadClass class-loader class-name)
        signature (into-array Class (or signature []))
        method (.getDeclaredMethod class method signature)]
    (.invoke method class (into-array Object params))))

(defmacro invoke-in
  "Invoke class-method (with optional signature) inside the given classloader, passing the optional params."
  [class-loader class-method & args]
  (let [class (namespace class-method)
        method (name class-method)]
    `(invoke-in* ~class-loader ~class ~method ~@args)))

(defn printable? [object]
  (or (nil? object)
      (and (class object)
           (.getClassLoader (class object)))))

(defn eval-in* [class-loader form]
  (let [print-read-eval (fn [form]
                          (->> (pr-str form)
                               (invoke-in class-loader clojure.lang.RT/readString [String])
                               (invoke-in class-loader clojure.lang.Compiler/eval [Object])))]
    (with-classloader class-loader
                      (let [result (print-read-eval form)]
                        (if-not (printable? result)
                          result
                          (let [string (invoke-in class-loader clojure.lang.RT/printString [Object] result)]
                            (try
                              (read-string string)
                              (catch RuntimeException _
                                result))))))))

(defn eval-in [class-loader form]
  (eval-in* class-loader `(clojure.main/with-bindings (eval '~form))))

(defn ^URL path->url [path]
  (let [lib? (str/ends-with? path ".jar")]
    (io/as-url (str "file:" path (when-not lib? "/")))))

(defn create-class-loader [paths color-mode]
  (try
    (let [url-array (into-array URL (map path->url paths))
          ^URLClassLoader class-loader (url-classloader url-array ext-classloader)]
      ;; use Clojure 1.6+ API to properly initialize Clojure runtime:
      (.loadClass class-loader "clojure.java.api.Clojure")
      (eval-in* class-loader '(.invoke (clojure.java.api.Clojure/var "clojure.core" "require")
                                       (.invoke (clojure.java.api.Clojure/var "clojure.core" "symbol")
                                                "clojure.main")))
      class-loader)
    (catch Exception e
      (println (str (color/error color-mode "Couldn't create classloader for paths: ") paths " "
                    (color/error color-mode e))))))
