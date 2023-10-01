(ns ^:no-doc polylith.clj.core.util.interface.os
  (:require [polylith.clj.core.util.os :as os]))

(defn windows? []
  (os/windows-os?))
