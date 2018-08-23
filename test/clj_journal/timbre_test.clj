(ns clj-journal.timbre-test
  (:require [clj-journal.timbre :refer :all]
            [clojure.test :refer :all]
            [taoensso.timbre :as timbre]))

(deftest timbre
  (is (map? (journal-appender))))

;; FIXME: Should use the timbre logger for logging to journal, then read back
;; journal as JSON, unmarshal and verify that it contains what is expected.

(comment
  (timbre/merge-config! {:level     :trace
                         :appenders {:journal-appender (journal-appender)}})

  (timbre/info "ads" {:asd   12
                      :leet  1337
                      :weird {:blegh "lollo"}}))
