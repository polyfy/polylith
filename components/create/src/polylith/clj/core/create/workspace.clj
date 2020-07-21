(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]
            [clojure.string :as str]))

(defn readme-content [ws-name]
  ["<img src=\"images/logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
   ""
   "Polylith documentation can be found here:"
   ""
   "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
   "- The [Polylith Tool documentation](https://github.com/tengstrand/polylith/tree/core)"
   "- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)"
   ""
   "You can also get in touch with the Polylith Team via our [forum](https://polylith.freeflarum.com) or on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)."
   ""
   (str "<h1>" ws-name "</h1>")
   ""
   "<p>Add your workspace documentation here...</p>"])

(defn ->path [ws-path ws-name]
  (if (str/ends-with? ws-path "/")
    (str ws-path ws-name)
    (str ws-path "/" ws-name)))

(defn create [ws-path ws-name ws-ns]
  (let [path (->path ws-path ws-name)]
    (file/create-dir path)
    (file/create-dir (str path "/bases"))
    (file/create-dir (str path "/components"))
    (file/create-dir (str path "/environments"))
    (file/create-dir (str path "/images"))
    (file/create-file (str path "/deps.edn")
                      [(str "{:polylith {:vcs \"git\"")
                       (str "            :color-mode \"dark\"")
                       (str "            :top-namespace \"" ws-ns "\"")
                       (str "            :env-short-names {}}}")])
    (file/create-file (str path "/readme.md") (readme-content ws-name))
    (file/copy-resource-file! "create/logo.png" (str path "/images/logo.png"))
    (git/init ws-path)))
