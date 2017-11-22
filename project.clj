(defproject runejuhl/clj-journal "0.1.0"
  :description "Logging to systemd-journal using jna/native journal libraries."
  :url "https://github.com/runejuhl/clj-journal"
  :license {:name "GPL-3.0"
            :url  "http://www.gnu.org/licenses/gpl-3.0-standalone.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]

                 ;; for journald logging; also requires libsystemd to be
                 ;; installed
                 [net.java.dev.jna/jna "3.4.0"]])
