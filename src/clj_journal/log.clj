(ns clj-journal.log
  (:require [clj-journal.errno :refer [errno->string]]
            [clj-journal.systemd :refer [sd_journal_print sd_journal_send]]
            [clj-journal.util :refer :all]
            [clojure.string]))

(def ^:dynamic *strict*
  "Whether we should raise an exception on `%n` values in strings, or if we
  should silently replace it with `%_n` to avoid the \"%n in writable segment
  detected\" C printf error from killing the JVM."
  false)

(defn sanitize-format-string
  [s]
  (let [s* (clojure.string/replace s #"%n" "%_n")]
    (when (and (not= s s*)
               *strict*)
      (throw (ex-info "Invalid format string in log message"
                      {:string s})))
    s*))

(defn jprint
  "Submit simple plain text messages to the journal log.

  Returns a partial function if only `log-level` is given."
  ([log-level]
   (partial jprint log-level))
  ([log-level fmt & args]
   (->> (flatten [(level->syslog log-level) (sanitize-format-string fmt) args])
        (to-array)
        (.invoke sd_journal_print Integer)
        (errno->string))))

(defn jsend
  "Submit structured log messages to the journal log. `args` should be given as
  a sequence of string keywords and values.

  Returns a partial function if only `log-level` is given."
  ([log-level]
   (partial jsend log-level))
  ([log-level msg & args]
   {:pre [(-> args count even?)]}
   (->> args
        (concat [:message   (sanitize-format-string msg)
                 :priority (level->syslog log-level)])
        (apply args->journal-fields)
        ;; The varargs for the sd_journal functions needs to end with a null
        ;; byte
        (#(conj % 0x0))
        (to-array)
        (.invoke sd_journal_send Integer)
        (errno->string))))
