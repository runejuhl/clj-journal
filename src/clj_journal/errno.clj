(ns clj-journal.errno
  (:import [com.sun.jna Function]))

(def ^com.sun.jna.Function strerror
  (Function/getFunction "c" "strerror"))

(defn errno->string
  "Convert ISO C integer `errno` into a string describing the error. Returns nil
  if `errno` is 0."
  [errno]
  (when-not (zero? errno)
    (.invoke strerror String (to-array [errno]))))
