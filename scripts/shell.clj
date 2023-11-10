(ns shell
  "Support for launching shell cmds for bb scripts"
  (:require [babashka.fs :as fs]
            [babashka.process :as process]
            [clojure.string :as str]))

(defn homify [path]
  (let [home-dir (System/getProperty "user.home")]
    (if (str/starts-with? path home-dir)
      (str/replace-first path home-dir "~")
      path)))

(defn shell
  "Small wrapper for babashka/shell to:
  - pretty print out each cmd before it is run
  - support altering output via :alter-out-fn (assumes :out is file)"
  [& args]
  (let [[opts & args] (if (map? (first args))
                        args
                        (conj args {}))
        {:keys [out dir alter-out-fn]} opts
        opts (assoc opts :pre-start-fn (fn [o]
                                         (let [cmd-str (->> (:cmd o)
                                                               (mapv #(if (str/includes? % " ")
                                                                        (format  "\"%s\"" %)
                                                                        (homify %)))
                                                               (str/join " "))
                                               dir-str (if dir
                                                         (str dir)
                                                         (System/getProperty "user.dir"))
                                               out-fname (when (and out (not (keyword? out)))
                                                           (homify (str out)))]

                                           (println (format "┌─{%s}" (homify dir-str)))
                                           (if out-fname
                                             (println (format "├╼ %s\n└> %s" cmd-str out-fname))
                                             (println (format "└╼ %s" cmd-str))))))]
    (if alter-out-fn
      (->> (apply process/shell (assoc opts :out :string) args)
           alter-out-fn
           (spit out))
      (apply process/shell opts args))))

(defn run-java-jar [shell-opts jar args]
  (apply shell shell-opts
         "java -jar"
         (str jar)
         (process/tokenize args)))

(defn poly
  "Simulates poly stand-alone launcher"
  [shell-opts poly-cmd]
  (run-java-jar shell-opts (fs/absolutize "projects/poly/target/poly.jar") poly-cmd))

(defn polyx
  "Simulates poly stand-alone launcher"
  [shell-opts poly-cmd]
  (run-java-jar shell-opts (fs/absolutize "projects/polyx/target/polyx.jar") poly-cmd))

(comment
  (shell {:out "lee.txt"
          :dir ".."} "echo 'dingo bingo"))
