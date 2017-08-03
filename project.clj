;;;; Copyright © 2015-2017 José Pablo Fernández Silva

(defproject com.pupeno/free-form "0.6.0"
  :description "Library for building forms with Reagent or Re-frame."
  :url "https://github.com/pupeno/free-form"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :lein-release {:deploy-via :clojars}
  :signing {:gpg-key "71E6E789"}
  :scm {:name "git"
        :url  "https://github.com/pupeno/free-form"}

  :dependencies [[org.clojure/clojurescript "1.9.293" :scope "provided"]
                 [org.clojure/clojure "1.8.0" :scope "provided"]
                 [reagent "0.6.0" :scope "provided"]
                 [re-frame "0.8.0" :scope "provided"]
                 [doo "0.1.7" :scope "provided"]]
  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-doo "0.1.7"]]

  :source-paths ["src/cljs"]
  :cljsbuild {:builds {:test {:source-paths ["src/cljs" "test/cljs"]
                              :compiler     {:main          free-form.runner
                                             :output-to     "out/free_form.js"
                                             :optimizations :none}}}}

  :doo {:build "test"})
