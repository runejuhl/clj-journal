(defproject runejuhl/clj-journal "0.2.5"
  :description "Structured logging to systemd journal using native systemd libraries and JNA
(Java Native Access)"
  :url "https://github.com/runejuhl/clj-journal"
  :license {:name "GPL-3.0"
            :url  "http://www.gnu.org/licenses/gpl-3.0-standalone.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]

                 ;; for journald logging; also requires libsystemd to be
                 ;; installed
                 [net.java.dev.jna/jna "3.4.0"]]

  :eastwood {:exclude-linters [:constant-test]}

  :profiles
  {:dev
   {:dependencies [[com.taoensso/timbre "4.10.0"]
                   [org.clojure/data.json "0.2.6"]]}})
