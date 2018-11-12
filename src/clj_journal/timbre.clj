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
  "Journal appender for timbre. Optionally takes a single fn that should return
  a map that will be merged into the structured fields sent to journal.

  Any maps passed to a timbre logger will be sent to journal as structured data.
  Any complex data structures in values will be serialized as EDN."
  ([]
   (journal-appender (fn [{:keys [?file ?line ?ns-str] :as data}]
                       {"CODE_FILE" ?file
                        "CODE_LINE" ?line
                        "CODE_NS"   ?ns-str})))
  ([default-fields-fn]
   {:enabled?   true
    :async?     false
    :min-level  :trace
    :rate-limit nil
    :output-fn  :inherit
    :fn
    (fn [{:keys [instant level output_ vargs]
         :as   data}]
      (let [default-fields (default-fields-fn data)
            log-maps       (filter map? vargs)
            merged-map     (merge (apply merge log-maps) default-fields)]
        (apply jsend (concat [(timbre->syslog level) (force output_)]
                       (reduce concat merged-map)))))}))
