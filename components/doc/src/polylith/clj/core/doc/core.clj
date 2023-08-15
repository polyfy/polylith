(ns ^:no-doc polylith.clj.core.doc.core
  (:require [clojure.java.browse :as browse]
            [polylith.clj.core.version.interface :as ver]
            [polylith.clj.core.doc.navigation.more :as more]))

(defn doc-url [branch local? github?]
  (cond
    github? (if (= "master" branch)
             (str "https://github.com/polyfy/polylith/tree/master")
             (str "https://github.com/polyfy/polylith/blob/" branch))
    local? (str "http://localhost:8000/d/polylith/clj-poly/" ver/name)
    :else (str "https://cljdoc.org/d/polylith/clj-poly/" ver/name)))

(defn bookmark-url [page bookmark branch local? github?]
  (str (doc-url branch local? github?)
       (if github?
         (str "/doc/" page ".adoc")
         (str "/doc/reference/" page))
       "#" bookmark))

(defn page-url [page branch local? github?]
  (str (doc-url branch local? github?)
       (if (and github? (= "readme" page))
         "/" "/doc/")
       (or page "readme")
       (if github? ".adoc" "")))

(defn more-url [page local?]
  (or (get-in more/config (map keyword (conj page "url")))
      (page-url "readme" nil local? false)))

(defn command
  "Helper function that makes sure that we go to the beginning of the help
   or workspace structure, even if we only pass in 'help' or 'ws'."
  [command-name command unnamed-args]
  (if (contains? (set unnamed-args) command-name)
    ""
    command))

(defn open-doc [branch local? github? help more page ws unnamed-args]
  (let [branch (or branch "master")
        help (command "help" help unnamed-args)
        ws (command "ws" ws unnamed-args)
        url (cond
              help (bookmark-url "commands" help branch local? github?)
              more (more-url more local?)
              page (page-url page branch local? github?)
              ws (bookmark-url "workspace-structure" ws branch local? github?)
              :else (page-url nil branch local? github?))]
    (browse/browse-url url)))
