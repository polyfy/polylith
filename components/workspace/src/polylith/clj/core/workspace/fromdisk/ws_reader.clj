(ns ^:no-doc polylith.clj.core.workspace.fromdisk.ws-reader
  (:require [polylith.clj.core.common.interface :as common]))

(def file-extensions (if common/cljs?
                       ["clj" "cljs" "cljc"]
                       ["clj" "cljc"]))

(def reader
  {:name "polylith-clj"
   :project-url "https://github.com/polyfy/polylith"
   :language "Clojure"
   :type-position "postfix"
   :file-extensions file-extensions})
