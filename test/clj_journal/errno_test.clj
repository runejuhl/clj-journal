(ns clj-journal.errno-test
  (:require [clj-journal.errno :refer :all]
            [clojure.test :refer :all]))

(deftest errno
  (is (nil? (errno->string 0)))
  (is (= (errno->string 22) "Invalid argument")))
