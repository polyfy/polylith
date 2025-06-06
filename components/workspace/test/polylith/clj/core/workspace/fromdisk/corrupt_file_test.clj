(ns polylith.clj.core.workspace.fromdisk.corrupt-file-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [me.raynes.fs :as fs]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.fromdisk.namespaces-from-disk :as from-disk]))

(deftest empty-file--namespace-extraction--should-handle-error-with-path-info
  (let [temp-dir (str (fs/temp-dir "poly-test"))
        empty-file-path (str temp-dir "/empty_component.clj")]
    (try
      ;; Create an empty file
      (spit empty-file-path "")
      
      ;; Attempt to process the file - this should not throw an unhandled exception
      ;; but instead return a properly formatted error
      (let [namespace (from-disk/->namespace
                        temp-dir
                        #{"clj"}
                        temp-dir 
                        "test.core." 
                        "interface" 
                        empty-file-path)]
        
        ;; The namespace extraction should mark the file as invalid
        ;; but not fail entirely with an unhandled exception
        (is (map? namespace) "Should return a namespace map, not throw an exception")
        (is (:is-invalid namespace) "Empty file should be marked as invalid")
        (is (= "" (:namespace namespace)) "Empty file should have empty namespace string")
        (is (str/ends-with? empty-file-path (:file-path namespace)) "File path should be preserved")
        
        ;; Test the underlying file/read-file function directly
        (let [content (file/read-file empty-file-path #{"clj"})]
          (is (= content :polylith.clj.core.file.interface/empty-file) "Empty file should be handled gracefully")))
      
      (finally
        ;; Clean up
        (fs/delete-dir temp-dir)))))

(deftest real-file-loading--empty-file--should-return-error-with-file-path
  (let [temp-dir (str (fs/temp-dir "poly-test"))
        empty-file-path (str temp-dir "/src/test/core.clj")]
    (try
      ;; Create directory structure
      (fs/mkdirs (str temp-dir "/src/test"))
      ;; Create an empty file
      (spit empty-file-path "")
      
      ;; Try to load namespaces from this directory structure
      (let [namespaces (from-disk/namespaces-from-disk
                         temp-dir
                         #{"clj"}
                         [(str temp-dir "/src")]
                         []
                         "test."
                         "interface")]
        
        ;; The namespace extraction should succeed but mark the file as invalid
        (is (map? namespaces) "Should return a namespaces map")
        (is (vector? (:src namespaces)) "Should have src vector")
        (is (= 1 (count (:src namespaces))) "Should have one namespace")
        (let [ns-info (first (:src namespaces))]
          (is (:is-invalid ns-info) "Empty file namespace should be marked as invalid")
          (is (contains? ns-info :file-path) "Should contain file path")
          (is (not (nil? (:file-path ns-info))) "File path should not be nil")
          (is (:empty-file ns-info) "File should be marked as empty")))
      
      (finally
        ;; Clean up
        (fs/delete-dir temp-dir)))))

(deftest real-file-loading--corrupt-file--should-return-error-with-file-path
  (let [temp-dir (str (fs/temp-dir "poly-test"))
        empty-file-path (str temp-dir "/src/test/core.clj")]
    (try
      ;; Create directory structure
      (fs/mkdirs (str temp-dir "/src/test"))
      ;; Create an empty file
      (spit empty-file-path "(ns core)\n\n { \n\n\n\n\n ( \n\n\n\n } ")

      ;; Try to load namespaces from this directory structure
      (let [namespaces (from-disk/namespaces-from-disk
                         temp-dir
                         #{"clj"}
                         [(str temp-dir "/src")]
                         []
                         "test."
                         "interface")]

        ;; The namespace extraction should succeed but mark the file as invalid
        (is (map? namespaces) "Should return a namespaces map")
        (is (vector? (:src namespaces)) "Should have src vector")
        (is (= 1 (count (:src namespaces))) "Should have one namespace")

        (let [ns-info (first (:src namespaces))]
          (is (:is-invalid ns-info) "Corrupt file should be marked as invalid")
          (is (contains? ns-info :file-path) "Should contain file path")
          (is (not (nil? (:file-path ns-info))) "File path should not be nil")
          (is (= (:error-message ns-info) "Unmatched delimiter: }, expected: ) to match ( at [8 2]"))))

      (finally
        ;; Clean up
        (fs/delete-dir temp-dir)))))