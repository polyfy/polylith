(ns ^:no-doc polylith.clj.core.util.colors)

(def color-reset      "\u001B[0m")
(def color-black      "\u001B[30m")
(def color-cyan       "\u001B[36m")
(def color-blue       "\u001B[34m")
(def color-green      "\u001B[32m")
(def color-grey-light "\u001B[37m")
(def color-grey-dark  "\u001b[90m")
(def color-purple     "\u001B[35m")
(def color-red        "\u001B[31m")
(def color-white      "\u001B[37m")
(def color-yellow     "\u001B[33m")

(def color->code {:reset color-reset
                  :black color-black
                  :cyan color-cyan
                  :blue color-blue
                  :green color-green
                  :grey-light color-grey-light
                  :grey-dark color-grey-dark
                  :purple color-purple
                  :red color-red
                  :white color-white
                  :yellow color-yellow})
