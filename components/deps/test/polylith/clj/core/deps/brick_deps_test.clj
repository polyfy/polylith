(ns polylith.clj.core.deps.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.brick-deps :as brick-deps]))

(def environment {:deps {"workspace-clj" {:direct ["file" "util"], :indirect []},
                         "test-runner" {:direct ["util"], :indirect []}
                         "command" {:direct ["deps" "help" "util" "workspace"], :indirect ["file" "text-table" "user-config" "validate"]}
                         "text-table" {:direct ["util"], :indirect []}
                         "util" {:direct [], :indirect []}
                         "validate" {:direct ["deps" "util"], :indirect ["text-table"]}
                         "shell" {:direct [], :indirect []}
                         "workspace" {:direct ["deps" "file" "text-table" "user-config" "util" "validate"], :indirect []}
                         "cli" {:direct ["change" "file" "workspace"]
                                :indirect ["deps" "git" "shell" "text-table" "user-config" "util" "validate"]}
                         "user-config" {:direct [], :indirect []}
                         "git" {:direct ["shell"], :indirect []}
                         "deps" {:direct ["text-table" "util"], :indirect []}
                         "help" {:direct ["util"], :indirect []}
                         "file" {:direct [], :indirect []}
                         "z-jocke" {:direct ["change" "file" "util" "workspace"]
                                    :indirect ["deps" "git" "shell" "text-table" "user-config" "validate"]}
                         "common" {:direct ["util"], :indirect []}
                         "change" {:direct ["git" "util"], :indirect ["shell"]}}})

(def brick->color {"cli" :blue
                   "command" :green
                   "common" :green
                   "deps" :green
                   "file" :green
                   "text-table" :green
                   "user-config" :green
                   "util" :green
                   "validate" :green
                   "workspace" :green
                   "z-jocke" :blue})

(def brick->interface-deps {"workspace" ["common" "deps" "file" "text-table" "user-config" "util" "validate"]})

(deftest deps
  (is (= {:dependers [["command" :green]
                      ["cli" :blue]
                      ["z-jocke" :blue]],
          :dependees [["common" :yellow]
                      ["deps" :green]
                      ["file" :green]
                      ["text-table" :green]
                      ["user-config" :green]
                      ["util" :green]
                      ["validate" :green]]}
         (brick-deps/deps environment brick->color brick->interface-deps "workspace"))))
