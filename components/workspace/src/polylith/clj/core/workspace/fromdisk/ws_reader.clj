(ns ^:no-doc polylith.clj.core.workspace.fromdisk.ws-reader)

(def file-extensions ["clj" "cljs" "cljc"])

(def reader
  {:name "polylith-clj"
   :project-url "https://github.com/polyfy/polylith"
   :language "Clojure"
   :type-position "postfix"
   :file-extensions file-extensions})
