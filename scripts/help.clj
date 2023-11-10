(ns help
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [shell :refer [poly]]))

(def cmds [[":all" "help.txt"]
           ["check"]
           ["create"]
           ["create component"]
           ["create base"]
           ["create project"]
           ["create workspace"]
           ["deps"]
           ["deps :brick"]
           ["deps :project"]
           ["deps :workspace"]
           ["deps :project :brick"]
           ["diff"]
           ["doc"]
           ["info"]
           ["libs"]
           ["migrate"]
           ["overview"]
           ["shell"]
           ["switch-ws"]
           ["tap"]
           ["test"]
           ["version"]
           ["ws"]])

(defn help-fname [path cmd file]
  (fs/file path
           (or file
               (str
                 (str/join "-"
                           (str/split cmd #" :?"))
                 ".txt"))))

;; Task entrypoints

(defn help[]
  (doseq [[cmd fname] cmds]
    (println "---" cmd "---")
    (poly {:out (help-fname "scripts/output/help" cmd fname)}
          (format "help %s color-mode:none :fake-poly" cmd))))

(defn update-command-doc []
  (let [out "doc/commands.adoc"]
    (->> cmds
         (reduce (fn [acc [cmd fname]]
                   (str acc
                        (slurp (help-fname "scripts/output/help/begin" cmd fname))
                        (slurp (help-fname "scripts/output/help" cmd fname))
                        (slurp "scripts/output/help/end.txt")))
                 "")
         (spit out))
    (println "Wrote:" out)))
