(ns ^:no-doc polylith.clj.core.doc.core
  (:require [clojure.java.browse :as browse]
            [polylith.clj.core.version.interface :as ver]))

(def more-config {:high-level {:url "https://polylith.gitbook.io/polylith"}
                  :slack {:url "https://clojurians.slack.com/archives/C013B7MQHJQ"}
                  :python-tool {:url "https://davidvujic.github.io/python-polylith-docs"}
                  :blog-posts {:url "https://polylith.gitbook.io/polylith/#read-a-blog-post"
                               :a-fresh-take-on-monorepos-in-python-blog-post {:url "https://davidvujic.blogspot.com/2022/02/a-fresh-take-on-monorepos-in-python.html"}
                               :how-polylith-came-to-life {:url "https://medium.com/@joakimtengstrand/the-polylith-architecture-1eec55c5ebce"}
                               :the-monorepos-polylith-series {:url "https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2"}
                               :the-micro-monolith-architecture {:url "https://medium.com/@joakimtengstrand/the-micro-monolith-architecture-d135d9cafbe"}
                               :the-origin-of-complexity {:url "https://itnext.io/the-origin-of-complexity-8ecb39130fc"}}
                  :workspaces {:url "https://polylith.gitbook.io/polylith/#look-at-working-code"
                               :game-of-life-ws {:url "https://github.com/tengstrand/game-of-life"}
                               :polylith-ws {:url "https://github.com/polyfy/polylith"}
                               :realworld-ws {:url "https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/e6f7f200bc46e4e2595e123947eec442ad91c9ab"}
                               :usermanager-ws {:url "https://github.com/seancorfield/usermanager-example/tree/polylith"}}
                  :videos {:url "https://polylith.gitbook.io/polylith/conclusion/videos"
                           :polylith-in-a-nutshell {:url "https://youtu.be/Xz8slbpGvnk"}
                           :los-angeles-clojure-users-group {:url "https://youtu.be/_tpNKAv4fro"}
                           :the-last-architecture-you-will-ever-need {:url "https://youtu.be/pebwHmibla4"}
                           :a-fresh-take-on-monorepos-video {:url "https://www.youtube.com/watch?v=HU61vjZPPfQ"}
                           :polylith–a-software-architecture-based-on-lego-like-blocks {:url "https://www.youtube.com/watch?v=wy4LZykQBkY"}}
                  :podcasts {:url "https://polylith.gitbook.io/polylith/#listen-to-a-podcast"
                             :polylith-with-joakim-james-and-furkan {:url "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"
                                                                     :part {:url "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"
                                                                            :exclude-from-navigation? true}
                                                                     :part1 {:url "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"}
                                                                     :part2 {:url "https://podcasts.apple.com/se/podcast/s4-e22-polylith-with-joakim-james-and-furkan-part-2/id1461500416?i=1000507542984&l=en"}}}})

(defn include-key? [[key data]]
  (and (not= :url key)
       (-> data :exclude-from-navigation? not)))

(defn navigation [key config]
  (let [config-keys (keys (filter include-key? config))]
    (if (empty? config-keys)
      [key []]
      [key (into {} (map #(navigation % (config %))
                         config-keys))])))

(def more-navigation (second (navigation :root more-config)))

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
  (or (get-in more-config (map keyword (conj page "url")))
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
