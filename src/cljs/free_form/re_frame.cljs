;;;; Copyright © 2015-2017 José Pablo Fernández Silva

(ns free-form.re-frame
  (:require [free-form.core :as core]
            [re-frame.core :as re-frame]))

(defn form [& args]
  (let [event                    (nth args 2)
        re-frame-event-generator (fn [keys value]
                                   (let [event-v (cond
                                                   (fn? event) (event keys value)
                                                   (vector? event) (conj event keys value)
                                                   :else [event keys value])]
                                     (re-frame/dispatch event-v)))
        args                     (assoc (vec args) 2 re-frame-event-generator)]
    (into [core/form] args)))
