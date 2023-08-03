(ns ^:no-doc polylith.clj.core.help.doc
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Opens a document in a web browser.\n"
       "\n"
       "  poly doc [" (s/key "ARG" cm) "]\n"
       "\n"
       "    " (s/key "ARG" cm) " = (omitted)       -> Opens the poly tool readme.\n"
       "\n"
       "          command:" (s/key "COMMAND" cm) " -> Opens the help for the given poly " (s/key "COMMAND" cm) ",\n"
       "                             or the overall help if not given.\n"
       "\n"
       "          page:" (s/key "PAGE" cm) "       -> Opens the given " (s/key "PAGE" cm) " for the poly tool documentation.\n"
       "\n"
       "          ws:" (s/key "KEY" cm) "          -> Opens the workspace structure section in the poly tool\n"
       "                             documentation, and scrolls to the " (s/key "KEY" cm) " section.\n"
       "\n"
       "          more:" (s/key "TYPE" cm) "       -> Opens up the given blog-post, podcast, video, et cetera.\n"
       "\n"
       "  Example:\n"
       "    poly doc\n"
       "    poly doc command:check\n"
       "    poly doc page install\n"
       "    poly doc ws:settings\n"
       "    poly doc more:blog-posts\n"
       "    poly doc more:blog-posts:how-polylith-came-to-life\n"
       "    poly doc more:high-level\n"
       "    poly doc more:podcasts\n"
       "    poly doc more:podcasts:polylith-with-joakim-james-and-furkan\n"
       "    poly doc more:python-tool\n"
       "    poly doc more:slack\n"
       "    poly doc more:videos\n"
       "    poly doc more:videos:polylith-in-a-nutshell\n"
       "    poly doc more:workspaces:realworld"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")

  ; .......10|.......20|.......30|.......40|.......50|.......60|.......70|.......80|
  #__)


