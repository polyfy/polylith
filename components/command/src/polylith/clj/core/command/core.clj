(ns polylith.clj.core.command.core
  (:require [clojure.pprint :as pp]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.create.interfc :as create]
            [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.test-runner.interfc :as test-runner]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.interfc :as ws]))

(defn check [{:keys [messages settings] :as workspace}]
  (let [color-mode (:color-mode settings color/none)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

(defn create [workspace ws-path type arg1 arg2]
  (condp = type
    "w" (create/create-workspace ws-path arg1 arg2)
    "e" (create/create-environment workspace arg1)
    (println (str "Unknown type: " type))))

(defn specified? [name]
  (and (not= "-" name)
       (-> name str/blank? not)))

(defn deps [workspace environment-name brick-name]
  (let [color-mode (-> workspace :settings :color-mode)]
    (if (specified? environment-name)
      (if (specified? brick-name)
        (deps/print-brick-table workspace environment-name brick-name color-mode)
        (deps/print-workspace-brick-table workspace environment-name color-mode))
      (if (specified? brick-name)
        (deps/print-brick-ifc-table workspace brick-name color-mode)
        (deps/print-workspace-ifc-table workspace color-mode)))))

(defn info [workspace arg]
  (case arg
    "-dump" (pp/pprint workspace)
    "-loc" (ws/print-table workspace true)
    (ws/print-table workspace false)))

(defn help [workspace cmd]
  (let [color-mode (or (-> workspace :settings :color-mode) color/none)]
    (help/print-help cmd color-mode)))

(defn test-ws [workspace arg1 arg2]
  (if (= arg1 "-all")
    (test-runner/run workspace arg2 true)
    (test-runner/run workspace arg1 (= "-all" arg2))))

(defn valid-command? [workspace cmd arg1]
  (or (-> workspace nil? not)
      (nil? cmd)
      (= "help" cmd)
      (and (= "create" cmd)
           (= "w" arg1))))

(defn execute [ws-path workspace cmd arg1 arg2 arg3]
  (try
    (if (valid-command? workspace cmd arg1)
      (case cmd
        "check" (check workspace)
        "create" (create workspace ws-path arg1 arg2 arg3)
        "deps" (deps workspace arg1 arg2)
        "help" (help workspace arg1)
        "info" (info workspace arg1)
        "test" (test-ws workspace arg1 arg2)
        (help workspace nil))
      (println "Only the 'help' and 'create w' commands can be executed outside a workspace."))
    {:ok? true}
    (catch Exception e
      {:ok? false
       :exception e})))
