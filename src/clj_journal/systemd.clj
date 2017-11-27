(ns clj-journal.systemd
  (:import [com.sun.jna Library Native Platform]))

(try
  (def ^com.sun.jna.Function sd_journal_print
    (com.sun.jna.Function/getFunction "systemd" "sd_journal_print"))
  (def ^com.sun.jna.Function sd_journal_send
    (com.sun.jna.Function/getFunction "systemd" "sd_journal_send"))
  (catch java.lang.UnsatisfiedLinkError ex
    (ex-info "Unable to open systemd library; is it installed?"
      {:cause :missing-library
       :hint  "The systemd library needs to be installed to be able to call the journal logging functions. On Debian-based platforms it can be installed with `apt-get install libsystemd`."} ex)))
