(ns polylith.clj.core.util.interfc.os
  (:require [polylith.clj.core.util.os :as os]))

(defn windows? []
  (os/windows-os?))
