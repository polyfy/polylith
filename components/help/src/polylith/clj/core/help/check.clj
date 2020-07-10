(ns polylith.clj.core.help.check
  (:require [polylith.clj.core.help.shared :as shared]
            [polylith.clj.core.util.interfc.color :as color]))

(defn help-text [color-mode]
  (str "  Checks if the workspace is valid or not. Checks that:\n"
       "  - " (color/error color-mode "Error 101:") " Components are only accessed vid their interface namespace, e.g.:\n "
       "   - " (shared/interface-ns "interface" color-mode) " \n "
       "   - " (shared/interface-ns "interface.subns" color-mode) " \n "
       "   - " (shared/interface-ns "interfc" color-mode) " \n "
       "   - " (shared/interface-ns "interfc.my.subns" color-mode) " \n"
       "  - " (color/error color-mode "Error 102:") " Functions and macros are only defined once in each interface.\n"
       "  - " (color/error color-mode "Error 103:") " All interfaces has a complete set of definitions (def/defn/defmacro).\n"
       "               A definition that is defined in one component's interface must also be\n"
       "               defined in all other components that implements that interface.\n"
       "  - " (color/error color-mode "Error 104:") " No circular dependencies exists.\n"
       "  - " (color/error color-mode "Error 105:") " No bases share the name of an interface or component.\n"
       "  - " (color/error color-mode "Error 106:") " Only one component per interface exists in each environment.\n"
       "  - " (color/error color-mode "Error 107:") " All environments has the components they need, based on referred interfaces.\n"
       "  - " (color/warning color-mode "Warning 201:") " Functions and macros for an interface has a matching parameter list\n"
       "                 (with the same name and order) in all implementing components."))

(defn print-help [color-mode]
  (-> color-mode help-text println))
