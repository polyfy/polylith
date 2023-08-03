(ns polylith.clj.core.doc.core
  (:require [clojure.java.browse :as browse]
            [clojure.string :as str]
            [polylith.clj.core.version.interface :as ver]))

(def doc-url (str "http://localhost:8000/d/polylith/clj-poly/" ver/name "/doc"))

(def blog-post "blog-post")
(def high-level "high-level")
(def podcast "podcast")
(def python-tool "python-tool")
(def slack "slack")
(def video "video")
(def workspaces "workspaces")

;; workspaces
(def game-of-life-ws "game-of-life")
(def polylith-ws "polylith")
(def realworld-ws "realworld")
(def usermanager-ws "usermanager")

;; blog posts
(def a-fresh-take-on-monorepos-in-python-blog-post "a-fresh-take-on-monorepos-in-python")
(def how-polylith-came-to-life "how-polylith-came-to-life")
(def the-monorepos-polylith-series "the-monorepos-polylith-series")
(def the-origin-of-complexity "the-origin-of-complexity")

;; videos
(def a-fresh-take-on-monorepos-video "a-fresh-take-on-monorepos-in-python")
(def los-angeles-clojure-users-group "los-angeles-clojure-users-group")
(def polylith–a-software-architecture-based-on-lego-like-blocks "polylith–a-software-architecture-based-on-lego-like-blocks")
(def polylith-in-a-nutshell "polylith-in-a-nutshell")
(def the-last-architecture-you-will-ever-need "the-last-architecture-you-will-ever-need")

;; podcasts
(def part "part")
(def part1 "part1")
(def part2 "part2")
(def polylith-with-joakim-james-and-furkan "polylith-with-joakim-james-and-furkan")

(def other->url {[high-level] "https://polylith.gitbook.io/polylith"
                 [slack] "https://clojurians.slack.com/archives/C013B7MQHJQ"
                 [python-tool] "https://davidvujic.github.io/python-polylith-docs"
                 [blog-post] "https://polylith.gitbook.io/polylith/#read-a-blog-post"
                 [blog-post a-fresh-take-on-monorepos-in-python-blog-post] "https://davidvujic.blogspot.com/2022/02/a-fresh-take-on-monorepos-in-python.html"
                 [blog-post how-polylith-came-to-life] "https://medium.com/@joakimtengstrand/the-polylith-architecture-1eec55c5ebce"
                 [blog-post the-monorepos-polylith-series] "https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2"
                 [blog-post the-origin-of-complexity] "https://itnext.io/the-origin-of-complexity-8ecb39130fc"
                 [workspaces] "https://polylith.gitbook.io/polylith/#look-at-working-code"
                 [workspaces game-of-life-ws] "https://github.com/tengstrand/game-of-life"
                 [workspaces polylith-ws] "https://github.com/polyfy/polylith"
                 [workspaces realworld-ws] "https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/e6f7f200bc46e4e2595e123947eec442ad91c9ab"
                 [workspaces usermanager-ws] "https://github.com/seancorfield/usermanager-example/tree/polylith"
                 [video] "https://polylith.gitbook.io/polylith/conclusion/videos"
                 [video polylith-in-a-nutshell] "https://youtu.be/Xz8slbpGvnk"
                 [video los-angeles-clojure-users-group] "https://youtu.be/_tpNKAv4fro"
                 [video the-last-architecture-you-will-ever-need] "https://youtu.be/pebwHmibla4"
                 [video a-fresh-take-on-monorepos-video] "https://www.youtube.com/watch?v=HU61vjZPPfQ"
                 [video polylith–a-software-architecture-based-on-lego-like-blocks] "https://www.youtube.com/watch?v=wy4LZykQBkY"
                 [podcast] "https://polylith.gitbook.io/polylith/#listen-to-a-podcast"
                 [podcast polylith-with-joakim-james-and-furkan] "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"
                 [podcast polylith-with-joakim-james-and-furkan part] "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"
                 [podcast polylith-with-joakim-james-and-furkan part1] "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894&l=en"
                 [podcast polylith-with-joakim-james-and-furkan part2] "https://podcasts.apple.com/se/podcast/s4-e22-polylith-with-joakim-james-and-furkan-part-2/id1461500416?i=1000507542984&l=en"})

(defn bookmark-url [page bookmark]
  (str doc-url "/reference/" page "#" bookmark))

(defn page-url [page]
  (str doc-url "/" (or page "readme")))

(defn more-url [page]
  (or (get other->url page (page-url "readme"))
      (page "readme")))

(defn open-doc [cmd command more page ws]
  (tap> {:more more})
  (let [cmd (when cmd (-> cmd (str/split #":") first))
        url (condp = cmd
                   "command" (bookmark-url "commands" command)
                   "more" (more-url more)
                   "page" (page-url page)
                   "ws" (bookmark-url "workspace-structure" ws)
                   (page-url "readme"))]
    (browse/browse-url url)))
