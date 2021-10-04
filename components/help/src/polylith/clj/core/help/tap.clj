(ns polylith.clj.core.help.tap
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Opens (or closes/cleans) a portal window (https://github.com/djblue/portal)\n"
       "  where " (s/key "tap>" cm) " statements are sent to. This command is used from the shell and\n"
       "  is mainly used internally when developing the poly tool itself.\n"
       "\n"
       "  tap [" (s/key "ARG" cm) "]\n"
       "    " (s/key "ARG" cm) " = " (s/key "(omitted)" cm) "  Opens a portal window.\n"
       "          " (s/key "open" cm) "       Opens a portal window.\n"
       "          " (s/key "close" cm) "      Closes the portal window\n"
       "          " (s/key "clear" cm) "      Clears the portal window\n"
       "\n"
       "  Example:\n"
       "    tap\n"
       "    tap open\n"
       "    tap clean\n"
       "    tap close"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
