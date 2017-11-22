(ns clj-journal.core
  (:import [com.sun.jna Library Native Platform]))

(def ^String library "systemd")
(try
  (def ^com.sun.jna.Function sd_journal_print
    (com.sun.jna.Function/getFunction library "sd_journal_print"))
  (def ^com.sun.jna.Function sd_journal_send
    (com.sun.jna.Function/getFunction library "sd_journal_send"))
  (catch java.lang.UnsatisfiedLinkError ex
    (ex-info "Unable to open systemd library; is it installed?"
      {:cause :missing-library
       :hint  "The systemd library needs to be installed to be able to call the journal logging functions. On Debian-based platforms it can be installed with `apt-get install libsystemd`."} ex)))

(def ^clojure.lang.PersistentList log-levels
  "Symbolic log levels. Based on `man syslog.2`:

   KERN_EMERG   0 System is unusable
   KERN_ALERT   1 Action must be taken immediately
   KERN_CRIT    2 Critical conditions
   KERN_ERR     3 Error conditions
   KERN_WARNING 4 Warning conditions
   KERN_NOTICE  5 Normal but significant condition
   KERN_INFO    6 Informational
   KERN_DEBUG   7 Debug-level messages
                8 Trace message (non-standard)"
  '(:emerg :alert :crit :err :warning :notice :info :debug :trace))

(defn level->syslog
  "Convert a keyword log level `k` into a syslog integer.

  Returns `k` if an invalid log level is given."
  [k]
  (let [i (.indexOf log-levels k)]
    (if (neg? i)
      k
      i)))

(defn args->journal-fields
  "Convert args into systemd journal fields (i.e. `PRIORITY=3`). Fields with
  `nil` values are pruned.

  Well-known fields are listed in man `systemd.journal-fields.7`."
  [& args]
  {:pre [(-> args count even?)]}
  (concat
    (reduce
      (fn [acc [k v]]
        (if-not (nil? v)
          (cons
            (str (clojure.string/upper-case (name k)) "=" v)
            acc)))
      (partition 2 args))
    ;; The varargs for the sd_journal functions needs to end with a null byte
    '(0x0)))

(defn jprint
  "Submit simple plain text messages to the journal log.

  Returns a partial function if only `log-level` is given."
  ([log-level]
   (partial jprint log-level))
  ([log-level format & args]
   (->> (flatten [(level->syslog log-level) format args])
     (to-array)
     (.invoke sd_journal_print Integer))))

(defn jsend
  "Submit structured log messages to the journal log. `args` should be given as
  a sequence of string keywords and values.

  Returns a partial function if only `log-level` is given."
  ([log-level]
   (partial jsend log-level))
  ([log-level msg & args]
   {:pre [(-> args count even?)]}
   (->> (concat [:message   msg
                 :log-level (level->syslog log-level)]
            args)
     (apply args->journal-fields)
     (to-array)
     (.invoke sd_journal_send Integer))))
