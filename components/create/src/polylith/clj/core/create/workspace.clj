(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]))

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

(defn create [ws-root-path ws-name ws-namespace]
  (if (nil? ws-namespace)
    (println "A namespace must be given.")
    (let [ws-path (str ws-root-path "/" ws-name)
          ws-ns (if (= "-" ws-namespace) "" ws-namespace)]
      (file/create-dir ws-path)
      (file/create-dir (str ws-path "/bases"))
      (file/create-dir (str ws-path "/components"))
      (file/create-dir (str ws-path "/environments"))
      (file/create-dir (str ws-path "/images"))
      (file/create-file (str ws-path "/deps.edn")
                        [(str "{:polylith {:vcs \"git\"")
                         (str "            :top-namespace \"" ws-ns "\"")
                         (str "            :interface-ns \"interface\"")
                         (str "            :env-aliases {\"development\" \"dev\"}}}")])
      (file/create-file (str ws-path "/readme.md") (readme-content ws-name))
      (file/copy-resource-file! "create/logo.png" (str ws-path "/images/logo.png"))
      (env/create-env ws-path "development")
      (git/init ws-root-path))))
