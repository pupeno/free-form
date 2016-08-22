;;;; Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

(defproject com.carouselapps/free-form "0.2.1"
  :description "Library for building forms with Reagent or Re-frame. This library changed namespace to com.pupeno/free-form"
  :url "https://github.com/pupeno/free-form"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :lein-release {:deploy-via :clojars}
  :signing {:gpg-key "F2FB1C6F"}
  :scm {:name "git"
        :url  "https://github.com/pupeno/free-form"}

  :dependencies [[org.clojure/clojurescript "1.7.48"]
                 [com.carouselapps/re-frame "0.4.1"]
                 #_[org.clojure/clojure "1.6.0"]]

  :source-paths ["src/cljs"])
