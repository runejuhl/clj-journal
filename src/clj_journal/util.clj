(ns clj-journal.util)

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

(defn ^:dynamic level->syslog
  "Convert a keyword log level `k` into an integer representing a syslog log
  level. If `k` is an integer it's returned as-is.

  Throws an IllegalArgumentException if given an invalid log level keyword."
  [k]
  (if (integer? k)
    k
    (let [i (.indexOf log-levels k)]
      (if (neg? i)
        (throw
         (IllegalArgumentException.
          (format "Invalid log level '%s'" k)))
        i))))

(defprotocol FieldName
  "Turn anything into a string."
  (stringify [_]))

(extend-protocol FieldName
  String
  (stringify [s] s)
  clojure.lang.Keyword
  (stringify [k] (name k))
  Object
  (stringify [o] (str o)))

(defn to-field-name
  "Stringify arbitrary objects to use as journal field names.

  Prefixes invalid field names (i.e. not starting with a letter) with \"X_\" to
  allow passing arbitrary names to journal."
  [o]
  (let [n              (stringify o)
        N              (clojure.string/upper-case n)
        sanitized-name (if (re-find #"^[A-Z]" N)
                         N
                         (str "X_" N))]
    sanitized-name))

(defn args->journal-fields
  "Convert args into systemd journal fields (i.e. `PRIORITY=3`). Fields with
  `nil` values are pruned.

  Well-known fields are listed in man `systemd.journal-fields.7`."
  [& args]
  {:pre [(-> args count even?)]}
  (->> args
       (partition 2)
       (filter (comp not nil? second))
       (map
        (fn [[k v]]
          (str (to-field-name k) "=" v)))
       (into [])))
