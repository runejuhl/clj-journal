(ns clj-journal.util-test
  (:require [clj-journal.util :refer :all]
            [clojure.test :refer :all]))

(deftest utility
  (is (= (args->journal-fields "some" 12 "value" "message")
         ["%s" "SOME=12" "%s" "VALUE=message"])
      "Should return a vector of strings")

  (is (thrown? AssertionError (args->journal-fields "some" 12 "value"))
      "Should throw an AssertionError when passed an uneven number of arguments")

  (is (= (level->syslog :err) 3) "`err` is syslog level 3")
  (is (= (level->syslog 3) 3) "Integer arguments are returned as-is")
  (is (thrown? java.lang.IllegalArgumentException
               (level->syslog :notanerrorlevel))
      "Should thrown an exception on invalid log level"))

(deftest names
  (is (= (to-field-name :active-check) "ACTIVE_CHECK"))
  (is (= (to-field-name :active/check) "ACTIVE_CHECK"))
  (is (= (to-field-name :active-_/_-%check) "ACTIVE______CHECK")))
