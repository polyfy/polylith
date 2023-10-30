(ns clean
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(defn -main []
  (println "Deleting (d=deleted -=did not exist)")
  (let [projects (fs/list-dir "projects")]
    (println "\nPolyth dirs:")
    (run! (fn [d]
            (println (format "[%s] %s"
                             (if (fs/exists? d) "d" "-")
                             d))
            (fs/delete-tree d))
          (into [".cpcache"]
                (mapv #(fs/file % "target") projects))))

  (let [substr "polylith-example-"
        temp-dirs (fs/list-dir (fs/temp-dir)
                               (fn accept [p]
                                 (str/starts-with? (fs/file-name p) substr)))]
    (println (format "\nTemp dirs matching %s:" substr))
    (if (seq temp-dirs)
      (run! (fn [d]
              (println (format "[d] %s" d))
              (fs/delete-tree d))
            temp-dirs)
      (println "[-] none found"))))
