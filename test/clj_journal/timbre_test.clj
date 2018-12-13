(ns clj-journal.timbre-test
  (:require [clj-journal.timbre :refer :all]
            [clj-journal.test.util :refer [get-journal-entry]]
            [clojure.test :refer :all]
            [taoensso.timbre :as timbre]))

(defmacro log
  "Wrapper around `timbre/log` that injects a random identifier and returns a
  matching log entry."
  ([level msg & args]
   `(let [id# (apply str (take 16 (repeatedly #(rand-nth "abcdefghijkl"))))]
      (timbre/log ~level ~msg {:clj_journal_identifier id#} ~@args)
      (get-journal-entry id#))))

(defn test-fixture
  "Test fixture used to set up timbre before testing."
  []
  (fn [f]
    (timbre/merge-config! {:level     :trace
                           :appenders {:journal-appender (journal-appender)}})
    (f)))

(use-fixtures :once (test-fixture))

(deftest timbre
  (is (map? (journal-appender))
    "journal-appender should return a map")

  (is (= "1"                            ;1 is alert
        (:PRIORITY
         (binding [clj-journal.timbre/timbre->syslog (fn [_] 1)]
           (log :debug "hello there, this is an alert"))))
    "The timbre->syslog level conversion can be overridden"))

(deftest logging-tests
  (is (= "12" (:ASD (log :info "hello there" {:asd 12})))
    "We can retrieve data sent to journal")

  (is (= "42" (:WTF (log :info "hello there" {:asd 12} {:wtf 42})))
    "Logging functions may pass multiple hash-maps")

  (is (= "42" (:WTF (log :info "hello there" {:wtf 12} {:wtf 42})))
    "Right-most hash-map wins")

  (is (= {:WTF "42"
          :ASD "12"}
        (select-keys
          (log :info "hello there" {:asd 12} {:wtf 42})
          #{:WTF :ASD}))
    "Logging functions may pass multiple hash-maps")

  (is (= "42" (:WTF (log :info "hello there" {1 (fn [] "omg")} {:wtf 42})))
    "Hash-map keys may be of arbitrary types")

  (let [f      (fn [] "omg")
        fs     (str f)
        result {:X_133ASD "1231"
                :A1       fs
                :WTF      "42"}]
    (is
      (= result
        (select-keys
          (log :info "hello there" {:a1 f} {:wtf 42 :133asd 1231})
          (keys result)))
      "Invalid keys are automatically coerced and sanitized")))
