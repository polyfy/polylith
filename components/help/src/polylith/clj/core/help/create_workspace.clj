(ns polylith.clj.core.help.create-workspace
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a workspace.\n"
       "\n"
       "  poly create w name:" (s/key "NAME" cm) " top-ns:" (s/key "TOP-NAMESPACE" cm) "\n"
       "    " (s/key "NAME" cm) " = The name of the workspace to create.\n"
       "\n"
       "    " (s/key "TOP-NAMESPACE" cm) " = The top namespace, e.g. com.my.company.\n"
       "\n"
       "  Example:\n"
       "    poly create w name:myws top-ns:com.my.company"
       "    poly create workspace name:myws top-ns:com.my.company"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
