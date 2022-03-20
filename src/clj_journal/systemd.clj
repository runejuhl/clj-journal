(ns clj-journal.systemd
  (:import [com.sun.jna Function]))

(try
  (def ^com.sun.jna.Function sd_journal_print
    (Function/getFunction "systemd" "sd_journal_print"))
  (def ^com.sun.jna.Function sd_journal_send
    (Function/getFunction "systemd" "sd_journal_send"))
  (catch java.lang.UnsatisfiedLinkError ex
    (ex-info "Unable to open systemd library; is it installed?"
             {:cause :missing-library
              :hint  (str
                      "The systemd library needs to be installed to be able to call "
                      "the journal logging functions. On Debian-based platforms it "
                      "can be installed with `apt-get install libsystemd`.")}
             ex)))
