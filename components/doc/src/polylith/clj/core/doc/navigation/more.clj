(ns polylith.clj.core.doc.navigation.more
  (:require [polylith.clj.core.doc.navigation.calculator :as calculator]))

(def config {:high-level {:url "https://polylith.gitbook.io/polylith"
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

(def navigation (calculator/navigation config))
