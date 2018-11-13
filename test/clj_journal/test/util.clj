(ns clj-journal.test.util
  (:require [clojure.data.json :as json]
            [clojure.java.shell :as sh]))

(defn get-journal-entry
  "Get a single journal entry with a particular ID."
  [id]
  (let [{:keys [out exit] :as entry}
        (sh/sh "journalctl" "-n1"  "--output=json" (str "CLJ_JOURNAL_IDENTIFIER=" id))]
    (if (and (zero? exit) (not (empty? out)))
      (json/read-str out
        :key-fn keyword))))
