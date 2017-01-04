;;;; Copyright © 2017 José Pablo Fernández Silva

(ns free-form.debug
  (:require [free-form.extension :as extension]))

(defmethod extension/extension :debug [_extension-name inner-fn]
  (fn [html]
    (println "Before:" html)
    (let [html (inner-fn html)]
      (println "After" html)
      html)))
