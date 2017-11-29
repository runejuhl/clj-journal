(ns clj-journal.timbre
  (:require [clj-journal.log :refer [jsend]]))

(def timbre->syslog-map
  {:trace  7                       ;debug
   :debug  7                       ;debug
   :info   6                       ;info
   :warn   4                       ;warning
   :error  3                       ;err
   :report 1                       ;alert
   :fatal  0                       ;emerg
   })

(defn ^:dynamic timbre->syslog
  "Convert Timbre log level into the syslog equivalent.
  Defaults to log level 3 (err) if passed an invalid log level."
  [k]
  (get timbre->syslog-map k 3))

(defn journal-appender
  "Journal appender for timbre."
  []
  {:enabled?   true
   :async?     false
   :min-level  :trace
   :rate-limit nil
   :output-fn  :inherit
   :fn
   (fn [{:keys [instant level output_ ?file ?line ?ns-str]
        :as   data}]
     (jsend (timbre->syslog (:level data)) (force output_)
       "CODE_FILE" ?file
       "CODE_LINE" ?line
       "CODE_NS"   ?ns-str))})
