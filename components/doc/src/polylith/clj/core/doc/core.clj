(ns ^:no-doc polylith.clj.core.doc.core
  (:require [clojure.java.browse :as browse]
            [polylith.clj.core.version.interface :as ver]
            [polylith.clj.core.doc.navigation.more :as more]))

(defn doc-url [branch local?]
  (cond
    branch (if (= "master" branch)
             (str "https://github.com/polyfy/polylith/tree/master")
             (str "https://github.com/polyfy/polylith/blob/" branch))
    local? (str "http://localhost:8000/d/polylith/clj-poly/" ver/name)
    :else (str "https://cljdoc.org/d/polylith/clj-poly/" ver/name)))

(defn bookmark-url [page bookmark branch local?]
  (str (doc-url branch local?) "/doc/reference/" page
       (if branch ".adoc" "")
       "#" bookmark))

(defn page-url [page branch local?]
  (str (doc-url branch local?)
       (if (and branch (= "readme" page))
         "/" "/doc/")
       (or page "readme")
       (if branch ".adoc" "")))

(defn more-url [page local?]
  (or (get-in more/config (map keyword (conj page "url")))
      (page-url "readme" nil local?)))

(defn command
  "Helper function that makes sure that we go to the beginning of the help
   or workspace structure, even if we only pass in 'help' or 'ws'."
  [command-name command unnamed-args]
  (if (contains? (set unnamed-args) command-name)
    ""
    command))

(defn open-doc [branch local? help more page ws unnamed-args]
  (let [help (command "help" help unnamed-args)
        ws (command "ws" ws unnamed-args)
        url (cond
              help (bookmark-url "commands" help branch local?)
              more (more-url more local?)
              page (page-url page branch local?)
              ws (bookmark-url "workspace-structure" ws branch local?)
              :else (page-url nil branch local?))]
    (browse/browse-url url)))
