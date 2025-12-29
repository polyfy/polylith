(ns ^:no-doc polylith.clj.core.doc.navigation.more
  (:require [polylith.clj.core.doc.navigation.calculator :as calculator]))

(def clojurescript-podcast-part1 "https://podcasts.apple.com/se/podcast/s4-e21-polylith-with-joakim-james-and-furkan-part-1/id1461500416?i=1000505948894")

(def config {:high-level {:url "https://polylith.gitbook.io/polylith"
                          :sharing-code {:url "https://polylith.gitbook.io/polylith/introduction/sharing-code"}
                          :testing-incrementally {:url "https://polylith.gitbook.io/polylith/introduction/testing-incrementally"}
                          :polylith-in-a-nutshell {:url "https://polylith.gitbook.io/polylith/introduction/polylith-in-a-nutshell"}
                          :workspace {:url "https://polylith.gitbook.io/polylith/architecture/2.1.-workspace"}
                          :component {:url "https://polylith.gitbook.io/polylith/architecture/2.3.-component"}
                          :base {:url "https://polylith.gitbook.io/polylith/architecture/2.3.-component"}
                          :project {:url "https://polylith.gitbook.io/polylith/architecture/2.6.-project"}
                          :development-project {:url "https://polylith.gitbook.io/polylith/architecture/2.4.-development"}
                          :bring-it-all-together {:url "https://polylith.gitbook.io/polylith/architecture/bring-it-all-together"}
                          :simplicity {:url "https://polylith.gitbook.io/polylith/architecture/simplicity"}
                          :tool {:url "https://polylith.gitbook.io/polylith/tool/the-polylith-plugin"}
                          :current-architectures {:url "https://polylith.gitbook.io/polylith/conclusion/current-arcitectures"}
                          :advantages-of-polylith {:url "https://polylith.gitbook.io/polylith/conclusion/advantages-of-polylith"}
                          :transitioning-to-polylith {:url "https://polylith.gitbook.io/polylith/conclusion/should-you-convert-your-system"}
                          :production-systems {:url "https://polylith.gitbook.io/polylith/conclusion/production-systems"}
                          :why-the-name-polylith {:url "https://polylith.gitbook.io/polylith/conclusion/the-name"}
                          :videos {:url "https://polylith.gitbook.io/polylith/conclusion/videos"}
                          :faq {:url "https://polylith.gitbook.io/polylith/conclusion/faq"}
                          :who-made-this {:url "https://polylith.gitbook.io/polylith/conclusion/who-made-polylith"}}
             :slack {:url "https://clojurians.slack.com/archives/C013B7MQHJQ"}
             :python-tool {:url "https://davidvujic.github.io/python-polylith-docs"}
             :in-japanese {:url "https://zenn.dev/shinseitaro/books/clojure-polylith"}
             :blog-posts {:url "https://polylith.gitbook.io/polylith/#read-a-blog-post"
                          :understanding-polylith-through-the-lens-of-hexagonal-architecture {:url "https://tengstrand.github.io/blog/2023-11-01-understanding-polylith-through-the-lens-of-hexagonal-architecture.html"}
                          :leveraging-polylith-to-improve-consistency-reduce-complexity-and-increase-changeability {:url "https://medium.com/qantas-engineering-blog/leveraging-polylith-to-improve-consistency-reduce-complexity-and-increase-changeability-2031dd3d5f3d"}
                          :a-fresh-take-on-monorepos-in-python {:url "https://davidvujic.blogspot.com/2022/02/a-fresh-take-on-monorepos-in-python.html"}
                          :how-polylith-came-to-life {:url "https://tengstrand.github.io/blog/2018-10-02-how-polylith-came-to-life.html"}
                          :the-monorepos-polylith-series {:url "https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2"}
                          :the-micro-monolith-architecture {:url "https://tengstrand.github.io/blog/2016-12-28-the-micro-monolith-architecture.html"}
                          :the-origin-of-complexity {:url "https://tengstrand.github.io/blog/2019-09-14-the-origin-of-complexity.html"}
                          :tetris-playing-ai-the-polylith-way-1 {:url "https://tengstrand.github.io/blog/2025-12-28-tetris-playing-ai-the-polylith-way-1.html"}}
             :workspaces {:url "https://polylith.gitbook.io/polylith#look-at-working-code"
                          :demo-rama-electric {:url "https://github.com/jeans11/demo-rama-electric"}
                          :game-of-life {:url "https://github.com/tengstrand/game-of-life"}
                          :integrant-system {:url "https://github.com/polyfy/polylith/tree/master/examples/integrant-system"}
                          :polylith {:url "https://github.com/polyfy/polylith"}
                          :realworld {:url "https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/cljs-frontend"}
                          :usermanager {:url "https://github.com/seancorfield/usermanager-example/tree/polylith"}}
             :videos {:url "https://polylith.gitbook.io/polylith/conclusion/videos"
                      :polylith-in-a-nutshell {:url "https://youtu.be/Xz8slbpGvnk"}
                      :collaborative-learning-polylith {:url "https://youtu.be/_tpNKAv4fro"}
                      :developer-tooling-for-speed-and-productivity-in-2024 {:url "https://youtu.be/pVvuyaRDA58?si=rBFMEyGtspdmGV29&t=1333"}
                      :the-last-architecture-you-will-ever-need {:url "https://youtu.be/pebwHmibla4"}
                      :a-fresh-take-on-monorepos-in-python {:url "https://www.youtube.com/watch?v=HU61vjZPPfQ"}
                      :polylith–a-software-architecture-based-on-lego-like-blocks {:url "https://www.youtube.com/watch?v=wy4LZykQBkY"}
                      :how-the-polylith-repo-has-evolved-over-time {:url "https://youtu.be/cfVzy9iPpLg"}
                      :the-gentle-monorepo {:url "https://www.youtube.com/watch?v=k49496sH9aw"}}
             :podcasts {:url "https://polylith.gitbook.io/polylith/#listen-to-a-podcast"
                        :polylith-with-joakim-james-and-furkan {:url clojurescript-podcast-part1
                                                                :part {:url clojurescript-podcast-part1
                                                                       :exclude-from-navigation? true}
                                                                :part1 {:url clojurescript-podcast-part1}
                                                                :part2 {:url "https://podcasts.apple.com/se/podcast/s4-e22-polylith-with-joakim-james-and-furkan-part-2/id1461500416?i=1000507542984"}}}})

(def navigation (calculator/navigation config))
