(ns polylith.clj.core.doc.core
  (:require [clojure.java.browse :as browse]
            [clojure.string :as str]
            [polylith.clj.core.version.interface :as ver]))

(def doc-url (str "http://localhost:8000/d/polylith/clj-poly/" ver/name "/doc"))

(def other->url {:realworld "https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/e6f7f200bc46e4e2595e123947eec442ad91c9ab"})

(defn bookmark-url [page bookmark]
  (str doc-url "/reference/" page "#" bookmark))

(defn page-url [page]
  (str doc-url "/" (or page "readme")))

(defn other-url [other]
  (str (or (-> other keyword other->url)
           (page-url "readme"))))

(defn open-doc [cmd command other page ws]
  (let [cmd (when cmd (-> cmd (str/split #":") first))
        url (condp = cmd
                   "command" (bookmark-url "commands" command)
                   "other" (other-url other)
                   "page" (page-url page)
                   "ws" (bookmark-url "workspace-structure" ws)
                   (page-url "readme"))]
    (browse/browse-url url)))
