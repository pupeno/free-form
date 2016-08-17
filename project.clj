;;;; Copyright © 2015, 2016 José Pablo Fernández Silva, All rights reserved.

(defproject com.pupeno/free-form "0.3.1-SNAPSHOT"
  :description "Library for building forms with Reagent or Re-frame."
  :url "https://github.com/pupeno/free-form"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :lein-release {:deploy-via :clojars}
  :signing {:gpg-key "71E6E789"}
  :scm {:name "git"
        :url  "https://github.com/pupeno/free-form"}

  :dependencies [#_[org.clojure/clojurescript "1.7.48"]
                 #_[org.clojure/clojure "1.6.0"]]

  :source-paths ["src/cljs"])
