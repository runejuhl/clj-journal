(ns clj-journal.timbre-test
  (:require [clj-journal.timbre :refer :all]
            [clojure.test :refer :all]))

(deftest timbre
  (is (map? (journal-appender))))
