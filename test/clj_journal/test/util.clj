(ns clj-journal.test.util
  (:require [clojure.data.json :as json]
            [clojure.java.shell :as sh]))

(defn get-journal-entry
  "Get a single journal entry with a particular ID."
  [id]
  (let [{:keys [out exit] :as _entry}
        (sh/sh "journalctl" "-n1"  "--output=json" (str "CLJ_JOURNAL_IDENTIFIER=" id))]
    (when (and (zero? exit) (seq out))
      (json/read-str out
                     :key-fn keyword))))
