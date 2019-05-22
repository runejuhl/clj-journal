(ns clj-journal.log-test
  (:require [clj-journal.log :refer :all]
            [clojure.test :refer :all]))

(deftest logging
  (testing "print"
    (is (nil? (jprint :err "test! %s" "a string!")))
    (is (nil? (jprint :err "testing more: %x" 3735928559)))
    (is (nil? (jprint :err "testing more: %n" 3735928559)))

    (is (nil?
         (binding [clj-journal.util/level->syslog (fn [_] 1)]
           (jprint :debug "hello there"))))

    (is (thrown? IllegalArgumentException (jprint :not-a-log-level "booo"))))

  (testing "send"
    (is (nil? (jsend :err "test!")))
    (is (nil? (jsend :err "test! %n")))
    (is (thrown? IllegalArgumentException (jsend :not-a-log-level "booo")))))
